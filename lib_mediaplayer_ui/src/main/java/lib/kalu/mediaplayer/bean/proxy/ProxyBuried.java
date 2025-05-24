package lib.kalu.mediaplayer.bean.proxy;


import java.io.Serializable;

import lib.kalu.mediaplayer.args.StartArgs;

public interface ProxyBuried extends Serializable {

    void onCall(String name, StartArgs startArgs, long position, long duration);
}