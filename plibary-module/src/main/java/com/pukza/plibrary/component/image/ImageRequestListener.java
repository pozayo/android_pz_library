package com.pukza.plibrary.component.image;

/**
 * @author created by hwaseopchoi
 */

public interface ImageRequestListener {


    public void onException(Exception e, boolean b) ;
    public void onResourceReady(String id, boolean b1) ;
}
