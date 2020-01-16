/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.util.Color;
import com.revrobotics.ColorSensorV3;
import com.revrobotics.ColorMatchResult;
import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.ColorMatch;
import edu.wpi.first.wpilibj.Joystick;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  private final I2C.Port i2cPort = I2C.Port.kOnboard;
  /**
   * A Rev Color Sensor V3 object is constructed with an I2C port as a 
   * parameter. The device will be automatically initialized with default 
   * parameters.
   */
  private final ColorSensorV3 m_colorSensor = new ColorSensorV3(i2cPort);
  /**
   * A Rev Color Match object is used to register and detect known colors. This can 
   * be calibrated ahead of time or during operation.
   * 
   * This object uses a simple euclidian distance to estimate the closest match
   * with given confidence range.
   */
  private final ColorMatch m_colorMatcher = new ColorMatch();
  /**
   * Note: Any example colors should be calibrated as the user needs, these
   * are here as a basic example.
   */
  //commented values to the right of each color are from team 225's RI3D, try them out
  private final Color kBlueTarget = ColorMatch.makeColor(0.143, 0.427, 0.429);//(0.136, 0.412, 0.450)
  private final Color kGreenTarget = ColorMatch.makeColor(0.197, 0.561, 0.240);//(0.196, 0.557, 0.246)
  //red gives us the most trouble and errors right now
  private final Color kRedTarget = ColorMatch.makeColor(0.561, 0.232, 0.114);//(0.475, 0.371, 0.153)
  private final Color kYellowTarget = ColorMatch.makeColor(0.361, 0.524, 0.113);//(0.293, 0.561, 0.144)

  Joystick arcadeStick = new Joystick(0);
  TalonSRX WheelSpinner = new TalonSRX(5);
  DigitalInput limitSwitch = new DigitalInput(0);

  double magScale = .00025553;//((1/4096 * 3.14 * 4)/12) scaling factor to convert the raw encoder value to distance traveled
  double desiredDistance;
  double distanceTraveled = 0;
  double motorSpeed = .8; //.85 max

  String gameData = " ";
  Timer gameDataTimer = new Timer();
  double gameDataTimeOut = 3;

  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    m_colorMatcher.addColorMatch(kBlueTarget);
    m_colorMatcher.addColorMatch(kGreenTarget);
    m_colorMatcher.addColorMatch(kRedTarget);
    m_colorMatcher.addColorMatch(kYellowTarget);    
    WheelSpinner.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
    gameDataTimer.reset();
    	gameDataTimer.start();
    }

  public void Spin (double distance) {
    WheelSpinner.setSelectedSensorPosition(0);
    distanceTraveled = 0;
    desiredDistance = distance;
    while (desiredDistance > distanceTraveled) {
      distanceTraveled = WheelSpinner.getSelectedSensorPosition() * magScale;
      WheelSpinner.set(ControlMode.PercentOutput, motorSpeed);
      SmartDashboard.putNumber("Distance Traveled", distanceTraveled); 
    }
    WheelSpinner.set(ControlMode.PercentOutput, 0);
  }

  @Override
  public void robotPeriodic() {
    Color detectedColor = m_colorSensor.getColor();
    String colorString;
    ColorMatchResult match = m_colorMatcher.matchClosestColor(detectedColor);

    if (match.color == kBlueTarget) {
      colorString = "Blue";
    } else if (match.color == kRedTarget) {
      colorString = "Red";
    } else if (match.color == kGreenTarget) {
      colorString = "Green";
    } else if (match.color == kYellowTarget) {
      colorString = "Yellow";
    } else {
      colorString = "Unknown";
    }

    SmartDashboard.putNumber("Red", detectedColor.red);//tune the RGB values from the colorMatch
    SmartDashboard.putNumber("Green", detectedColor.green);
    SmartDashboard.putNumber("Blue", detectedColor.blue);
    SmartDashboard.putNumber("Confidence", match.confidence);
    SmartDashboard.putString("Detected Color", colorString);
    SmartDashboard.putString("FMS Color", gameData);
    //encoder outputs
    SmartDashboard.putNumber("Encoder Value", WheelSpinner.getSelectedSensorPosition());//raw
    SmartDashboard.putNumber("Distance Traveled", distanceTraveled);//scaled
  }
  
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    WheelSpinner.set(ControlMode.PercentOutput, 0);

  while (limitSwitch.get() == false ) {
    if (arcadeStick.getRawButton(1)) {
      Spin(26); //24ft is 3 rotations around the color wheel
  } else {
    WheelSpinner.set(ControlMode.PercentOutput, 0);
  }
  //reset the encoder
    if (arcadeStick.getRawButton(2)) {
      WheelSpinner.setSelectedSensorPosition(0);
  } else {
      WheelSpinner.getSelectedSensorPosition();
  }
  WheelSpinner.set(ControlMode.PercentOutput, arcadeStick.getY());
  }
}

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
