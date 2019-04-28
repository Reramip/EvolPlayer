package pers.jyb.evolplayer;

public class Communication {
    private boolean isPlayerAcitivityResuming;

    private static Communication communication;

    public boolean isPlayerAcitivityResuming() {
        return isPlayerAcitivityResuming;
    }

    public void setPlayerAcitivityResuming(boolean playerAcitivityResuming) {
        isPlayerAcitivityResuming = playerAcitivityResuming;
    }

    private Communication(){
        isPlayerAcitivityResuming=false;
    }

    public static Communication get(){
        if(communication==null){
            communication=new Communication();
        }
        return communication;
    }
}
