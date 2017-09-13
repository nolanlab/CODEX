package com.akoya.codex.upload;


public class EncryptUtils {

    public static final String DEFAULT_ENCODING = "UTF-8";
   

    public static void main(String[] args) {
        /*String txt = "some text to be encrypted";
        String key = "key phrase used for XOR-ing";
        System.out.println(txt + " XOR-ed to: " + (txt = xorMessage(txt, key)));

        String encoded = base64encode(txt);       
        System.out.println(" is encoded to: " + encoded + " and that is decoding to: " + (txt = base64decode(encoded)));
        System.out.print("XOR-ing back to original: " + xorMessage(txt, key));*/

        String msg = "P@ssw0rd.123";

        String key = "encoder";

        System.out.println(msg);
        msg = xorMessage(msg, key);
        System.out.println(msg);
        msg = xorMessage(msg, key);
        System.out.println(msg);

    }

    public static String xorMessage(String message, String key) {
        try {
            if (message == null || key == null) {
                return null;
            }
            char[] keys = key.toCharArray();
            char[] mesg = message.toCharArray();

            int ml = mesg.length;
            int kl = keys.length;
            char[] newmsg = new char[ml];

            for (int i = 0; i < ml; i++) {
                newmsg[i] = (char) (mesg[i] ^ keys[i % kl]);
            }//for i

            return new String(newmsg);
        } catch (Exception e) {
            return null;
        }
    }//xorMessage
}//class
