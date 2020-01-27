/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.util.Map;
import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.*;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.MedianFilter;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.shuffleboard.*;

/**
 * This is a sample program demonstrating how to use an ultrasonic sensor and
 * proportional control to maintain a set distance from an object.
 */

public class Robot extends TimedRobot {
  // factor to convert sensor values in cm to a distance in inches
  private static final double kValueToInches = 1/2.54;
  
  private static final int kRightMotorPort = 1;
  private static final int kUltrasonicPort = 0;

  // median filter to discard outliers; filters over 10 samples
  private final MedianFilter m_filter = new MedianFilter(10);
  
  private final AnalogInput m_ultrasonic = new AnalogInput(kUltrasonicPort);
  private TalonSRX m_RightMotor = new TalonSRX(kRightMotorPort);
  public Boolean BallPresent = false;  

  @Override
  public void robotPeriodic() {
    // returned value is filtered with a rolling median filter, since ultrasonics
    // tend to be quite noisy and susceptible to sudden outliers
    double currentDistance = m_filter.calculate(m_ultrasonic.getValue()) * kValueToInches;

    if (currentDistance<=6){
      BallPresent = true;
      //stop motor if ultrasonic sensor median falls below 6 inches indicating a ball is blocking the sensor
      m_RightMotor.set(ControlMode.PercentOutput, 0);
    } else {
      BallPresent = false;
      //stop motor if ultrasonic sensor median falls below 6 inches indicating a ball is blocking the sensor
      m_RightMotor.set(ControlMode.PercentOutput, 0.5);
    }
    Shuffleboard.getTab("Shooter").add("Ball Present", BallPresent)
                                  .withWidget(BuiltInWidgets.kBooleanBox)
                                  .withPosition(0, 0)
                                  .withSize(1, 1)
                                  .withProperties(Map.of("colorWhenTrue","green","colorWhenFalse","black"));
    Shuffleboard.getTab("Shooter").add("Ultrasonic 1", currentDistance)
                                  .withWidget(BuiltInWidgets.kGraph)
                                  .withPosition(0, 2)
                                  .withSize(4, 4);
    // convert distance error to a motor speed
    //double currentSpeed = (kHoldDistance - currentDistance) * kP;

    // drive robot
    //m_robotDrive.arcadeDrive(currentSpeed, 0);
  }
}
