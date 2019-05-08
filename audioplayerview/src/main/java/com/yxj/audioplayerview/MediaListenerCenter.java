package com.yxj.audioplayerview;

import android.util.SparseArray;

import com.yxj.audioplayerview.listener.Listener;

import java.lang.ref.WeakReference;

/**
 * Author:  Yxj
 * Time:    2019/5/7 上午10:48
 * -----------------------------------------
 * Description: 事件中心，本质上是一个一个监听View的集合，所以用弱引用
 */
class MediaListenerCenter {

    SparseArray<WeakReference<Listener>> listenerArray;

    private MediaListenerCenter(){
        listenerArray = new SparseArray<>();
    }

    private static MediaListenerCenter mInstance;

    public static MediaListenerCenter getInstance(){
        if(mInstance == null){
            synchronized (MediaListenerCenter.class){
                if(mInstance == null){
                    mInstance = new MediaListenerCenter();
                }
            }
        }
        return mInstance;
    }

    // 数据订阅者的注册
    public void addListener(int viewAndUriHash,Listener listener){
        listenerArray.put(viewAndUriHash,new WeakReference<Listener>(listener));
    }

    // 数据订阅者的注销
    public void removeListener(int viewAndUriHash){
        listenerArray.remove(viewAndUriHash);
    }

    public void sendTimeProgressEvent(int uriHash,int seconds){
        WeakReference<Listener> weakReference = listenerArray.get(uriHash);
        if(weakReference!=null){
            Listener listener = weakReference.get();
            if(listener!=null){
                listener.onTimeProgressListener(seconds);
            }
        }
    }

    public void sendBufferingUpdateEvent(int uriHash,int percent){
        WeakReference<Listener> weakReference = listenerArray.get(uriHash);
        if(weakReference!=null){
            Listener listener = weakReference.get();
            if(listener!=null){
                listener.onBufferingUpdate(percent);
            }
        }
    }

    public void sendDurationUpdateEvent(int uriHash,int seconds){
        WeakReference<Listener> weakReference = listenerArray.get(uriHash);
        if(weakReference!=null){
            Listener listener = weakReference.get();
            if(listener!=null){
                listener.onDurationListener(seconds);
            }
        }
    }

    public void sendonCompleteEvent(int uriHash){
        WeakReference<Listener> weakReference = listenerArray.get(uriHash);
        if(weakReference!=null){
            Listener listener = weakReference.get();
            if(listener!=null){
                listener.onCompleteListener();
            }
        }
    }

    public void sendReleaseEvent(int uriHash){
        WeakReference<Listener> weakReference = listenerArray.get(uriHash);
        if(weakReference!=null){
            Listener listener = weakReference.get();
            if(listener!=null){
                listener.onReleaseListener();
            }
        }
    }

    public void destroy(){
//        listenerArray = null;
    }

}
