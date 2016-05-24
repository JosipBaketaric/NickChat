# NickChat
NickChat is a small group chat app made for Android.
First user need's to register in order to use an app.
When registered, the user can log in to chat area and from there send and receive messages (text message or emoji).
All online users get all messages.
If the app is closed, notification is sent if the new message has arrived.
All messages are stored in the local database and can be deleted from there.


------------------------------------------------------------How It Works------------------------------------------------------------

When users type message and click on Send button PHP script is called which receives a nickname, message, and type of message.
PHP script then gets all online users from the database (Azure platform) and sends appropriate data to GCM (Google Cloud Messaging) which sends messages over a socket to every user (user is identified by a token which is provided by Google).
