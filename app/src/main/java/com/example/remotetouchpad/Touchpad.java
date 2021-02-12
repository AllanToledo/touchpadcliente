package com.example.remotetouchpad;

public class Touchpad {
    private final int screenWidth;
    private int initialX;
    private int initialY;
    private int actualX;
    private int actualY;

    private int wheelInitialY;
    private int wheelActualY;
    private int relativeWheelY;
    private int countRelativeWheelY = 0;

    private boolean isNewPosition;
    private long timerClick;
    private byte rigthClick;
    private byte leftClick;
    private long timerWheel;

    public Touchpad(int screenWidth) {
        this.screenWidth = screenWidth;
        reset();
    }

    public void setNewPosition(boolean newPosition) {
        isNewPosition = newPosition;
    }

    public void setInitialX(int initialX) {
        this.initialX = initialX;
    }

    public void setInitialY(int initialY) {
        this.initialY = initialY;
    }

    public void setInitialPosition(int initialX, int initialY) {
        this.reset();
        this.initialY = initialY;
        this.initialX = initialX;
        this.actualY = initialY;
        this.actualX = initialX;
        this.isNewPosition = true;
    }

    public void setActualPosition(int actualX, int actualY) {
        this.actualY = actualY;
        this.actualX = actualX;
        this.isNewPosition = false;
    }

    public void setActualX(int actualX) {
        this.actualX = actualX;
    }

    public void setActualY(int actualY) {
        this.actualY = actualY;
    }

    public int getRelativeX() {
        return actualX - initialX;
    }

    public int getRelativeY() {
        return actualY - initialY;
    }

    public void reset() {
        this.initialX = 0;
        this.actualX = 0;
        this.initialY = 0;
        this.actualY = 0;
        this.wheelActualY = 0;
        this.wheelInitialY = 0;
        this.relativeWheelY = 0;
        this.countRelativeWheelY = 0;
        this.leftClick = 0;
        this.rigthClick = 0;
    }

    public byte[] getPositions() {
        byte[] data = {
                (byte) (getRelativeX() >> 24),
                (byte) (getRelativeX() >> 16),
                (byte) (getRelativeX() >> 8),
                (byte) (getRelativeX()),
                (byte) (getRelativeY() >> 24),
                (byte) (getRelativeY() >> 16),
                (byte) (getRelativeY() >> 8),
                (byte) (getRelativeY()),
                (byte) (isNewPosition ? 1 : 0),
                leftClick,
                rigthClick,
                (byte) relativeWheelY
        };

        return data;
    }

    public void setTimerClick() {
        this.timerClick = System.currentTimeMillis();
    }

    public void verifyClick() {
        if (System.currentTimeMillis() - timerClick < 70) {
            if (initialX + 200 < screenWidth / 2) {
                this.leftClick = 1;
            } else if (initialX - 200 > screenWidth / 2) {
                this.rigthClick = 1;
            }
        }
    }

    public void setWheelInitialPosition(int y) {
        this.wheelInitialY = y;
    }

    public void setWheelActualPosition(int y) {
        if(countRelativeWheelY > 4){
            this.relativeWheelY = y - wheelInitialY > 0? -1 : 1;
            this.countRelativeWheelY = 0;
        } else {
            this.relativeWheelY = 0;
            this.countRelativeWheelY++;
        }
        this.wheelActualY = y;
    }

    public void wheelTimerReset() {
        this.timerWheel = System.currentTimeMillis();
    }

    public long getTimerWheel() {
        return timerWheel;
    }
}
