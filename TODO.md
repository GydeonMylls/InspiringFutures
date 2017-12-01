# To Do
(Roughly ordered by priority)

* [ ] **Fix file upload method** - Currently image files are not uploaded to the server. I believe the client side works so it's the PHP script that needs work (especially as I am not familiar with PHP). It has been suggested that a CSRF token needs to be implemented.
* [ ] **Expand ESM_AudioVideo to include audio and video** - The ESM_AudioVideo question type currently only allows pictures to be submitted, it should be fairly straighforward to expand this to cover audio and video recordings as well. There should also be options to add a previously taken photo, video, etc. and to remove a photo before submitting, but these are less urgent.
* [ ] **Settings** - The app needs a settings menu, currently a setting icon is displayed but only shows the licence info. Such a menu should include:
    * [ ] Device ID - Should be displayed to the used (but not be changeable)
    * [ ] Reminder time - An option to change the approximate reminder time
    * [ ] Change course - An option to change which course the user is participating in
    * [ ] Delete data - An option to delete all the questionnaire data stored on the phone
    * [ ] Contact - Contact details of someone involved in the project
    * [ ] Licence - Needs to be shown somewhere for legal reasons. (Should also be shown on startup, possibly just the first time the app is opened.)
* [ ] **Allow pushing questionnaires down to device** - At the moment questionnaires are fetched when the user chooses their programme then cannot be changed. It should be fairly simple to use a protocol such as MQTT to allow
    * [ ] One-time questionnaires - questionnaire that can be sent to users (on a prticular course) just once
    * [ ] Changes to daily questionnaire
* [ ] **Fix 'Other' option behaviour in ESM_Radios** - This is a minor bug but the 'Other' option should forget its user-entered text when deselected. I believe that currently if you deselect it by selecting another option this text remains.
* [ ] **Support screen rotation** - Again a bug, for some reason the 'Next' and 'Previous' buttons lose their text (and possibly functionality) when the device is rotated. Currently rotation is disabled (in the manifest I think) to prevent this from happening.
* [ ] **Different layout for tablets** - The current layout (one question per page) wastes lots of space on a large screeen. Instead it should show a scrollable list of questions with a submit button at the button. A similar layout might also work well for large phones in landscape orientation.
* [ ] **Tutorial** - Some form of tutorial for the user would be helpful when they first enter the app.

More technical tasks to be done are in TODO comments in code (most IDEs should be able to find them)
