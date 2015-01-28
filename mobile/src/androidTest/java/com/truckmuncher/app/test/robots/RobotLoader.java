package com.truckmuncher.app.test.robots;

public final class RobotLoader {
    private RobotLoader() {
        // No instances
    }

    public static <T extends ScreenRobot> T withRobot(Class<T> robot) {
        try {
            return robot.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("Unable to start robot: " + robot.getName());
    }
}
