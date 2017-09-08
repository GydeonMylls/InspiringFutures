/*
 * Copyright 2017 Gideon Mills
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.cam.crim.inspiringfutures.RemoteServer;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownServiceException;

import javax.net.ssl.HttpsURLConnection;

import uk.ac.cam.crim.inspiringfutures.LocalDatabase.LocalDatabaseHelper;
import uk.ac.cam.crim.inspiringfutures.LocalDatabase.LocalDatabaseSchema;
import uk.ac.cam.crim.inspiringfutures.LocalDatabase.ResponsesCursorWrapper;
import uk.ac.cam.crim.inspiringfutures.MainActivity;

/**
 * Manages connections to remote server
 *
 * <p> Created by  Gideon Mills on 15/08/2017 for InspiringFutures. </p>
 */

public class RemoteConnection {

    public static final String TAG = RemoteConnection.class.getSimpleName();
    public static final String USER_AGENT = "Inspiring Futures app ";

    private URL mServerURL;

    /**
     * @param urlString URL of server
     * @throws IOException If no protocol is specified or an unknown protocol is found
     */
    public RemoteConnection(@NonNull String urlString) throws IOException {
        mServerURL = new URL(urlString);
    }

    /**
     * Retrieves the text from a plaintext file on the server
     * @param filename    Name of file to be read, including relative path (exluding leading /) and extension
     * @return      Text in file
     * @throws IOException      If an I/O Exception occurs
     */
    public String getFileText(String filename) throws IOException {

        String filePath = mServerURL.toString()
                + "/"
                + filename;
        URL fileURL = new URL(
                filePath
            );

        URLConnection fileConnection = fileURL.openConnection();
        InputStream inStream = null;
        try {
            inStream = fileConnection.getInputStream();
        } catch (UnknownServiceException e) {
            e.printStackTrace();
        }
        StringBuilder stringBuilder = new StringBuilder();

        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            BufferedReader bInReader = new BufferedReader(
                    new InputStreamReader(
                            inStream
                    )
            );

            String inLine;
            while ( null != ( inLine = bInReader.readLine() ) ) {
                stringBuilder.append(inLine + "\n");
            }

        } finally {
            inStream.close();
        }

        return stringBuilder.substring(0, stringBuilder.length()-1 );   // Drops final newline character
    }

    /**
     * Sends an HTTP POST rquest with the provided parametres
     * @param postParams    Parametres
     * @return  true if the request was successful, false if not
     */
    private boolean sendPost(String postParams) {
        OutputStream outStream = null;
        try {
            HttpsURLConnection connection = (HttpsURLConnection) mServerURL.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", USER_AGENT + MainActivity.getDeviceId());
            connection.setDoOutput(true);

            // Send data
            outStream = connection.getOutputStream();
            Log.v(TAG, "Sending post request: " + postParams);
            outStream.write( postParams.getBytes() );
            outStream.flush();

            // Read response
            int responseCode = connection.getResponseCode();
            String response = connection.getResponseMessage();
            Log.d(TAG, "Transmitted response, got code '" + responseCode + "' and message '" + response + "'");
            return (HttpURLConnection.HTTP_OK == responseCode);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != outStream) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    // Shouldn't happen
                    e.printStackTrace();
                }
            }
        }
        // Only reach this if there is a problem
        return false;
    }

    /**
     * Sends reponses to remote server via HTTP POST requests, marking each response as transmitted on success. The remote connection must be using HTTPS.
     * @param cursor                Responses to be transmitted
     * @param localDatabaseHelper   Used to update tranmitted field
     */
    public void transmitResponses(Cursor cursor, LocalDatabaseHelper localDatabaseHelper) throws IOException {
        ResponsesCursorWrapper toSend = new ResponsesCursorWrapper( cursor );
        toSend.moveToFirst();
        while (!toSend.isAfterLast()) {
            String postParams = LocalDatabaseSchema.ResponsesTable.Columns.DEVICE_ID + "=" + toSend.getDeviceId() + "&"
                    + LocalDatabaseSchema.ResponsesTable.Columns.QUESTIONNAIRE_ID + "=" + toSend.getQuestionnaireId() + "&"
                    + LocalDatabaseSchema.ResponsesTable.Columns.TIMESTAMP + "=" + toSend.getTimestamp() + "&"
                    + LocalDatabaseSchema.ResponsesTable.Columns.RESPONSES + "=" + toSend.getResponsesString();
            boolean success = sendPost(postParams);
            if (success) {
                LocalDatabaseHelper.markAsTransmitted(toSend.getTimestamp(), localDatabaseHelper);
            }
            toSend.moveToNext();
        }
    }

}
