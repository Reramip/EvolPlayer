package pers.jyb.evolplayer;

class Communication {
    private static Communication communication;

    private boolean isServiceStarted;

    boolean isServiceStarted() {
        return isServiceStarted;
    }

    void setServiceStarted(boolean serviceStarted) {
        isServiceStarted = serviceStarted;
    }

    private Communication(){
        isServiceStarted=false;
    }

    static Communication get(){
        if(communication==null){
            communication=new Communication();
        }
        return communication;
    }
}
