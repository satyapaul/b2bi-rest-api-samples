# b2bi-rest-api-samples
Creating this repo to share some of the samples with larger group of B2Bi users.

Please edit the Java file and set the following java variables appropriately

    private static String apiUri = "/B2BAPIs/svc/messagebatches/";
    private static String serverUrl = "http://<ip-address>:45164";
	  private static boolean isSSL = false;

    private static String sourceFileLoc = "C:\\Users\\satyajit.paul\\Downloads\\";
    private static String fileName = "image001.PNG";  

    private static String mailboxPath = "/test123";

Successful execution will return following result

{
  "Location": "http://<ip-address>:45164/B2BAPIs/svc/messagebatches/78"
}

Please take the mailbox message ID and see if the file image001.PNG is updated succefully.

{
  "_id": "78",
  
  "_title": "MailboxMessage(78)",
  
  "$ref": http://<ip-address>:45164/B2BAPIs/svc/mailboxmessages/78,
	
  "href": http://<ip-address>:45164/B2BAPIs/svc/mailboxmessages/78,
	
  "actions": {
  
    "href": http://<ip-address>:45164/B2BAPIs/svc/mailboxmessages/78/actions,
	
    "$ref": http://<ip-address>:45164/B2BAPIs/svc/mailboxmessages/78/actions
    
  },
  
  "mailboxPath": "/test123",
  
  "mailboxId": 333,
  
  "name": "image001.PNG",
  
  "id": 78,
  
  "creationDate": "2020-03-14T17:19:20.000+0000",
  
  "extractableAlways": {
    "code": true,
    "display": "Yes"
  },
  "size": 17966,
  "documentId": "restclient1:node1:170d8cdabf4:17"
}