/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.akoya.codex.upload;

/**
 *
 * @author Nikolay Samusik
 */
public class CODEXwebToken {
    public final String acess_token;
     public final String token_type;
     public final int expires_in;
     public final String userName;
     public final String issued;
     public final String expires;

    public CODEXwebToken(String acess_token, String token_type, int expires_in, String userName, String issued, String expires) {
        this.acess_token = acess_token;
        this.token_type = token_type;
        this.expires_in = expires_in;
        this.userName = userName;
        this.issued = issued;
        this.expires = expires;
    }
     
}
