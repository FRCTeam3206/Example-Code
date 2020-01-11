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
import edu.wpi.first.wpilibj.DriverStation;
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
  private final Color kBlueTarget = ColorMatch.makeColor(0.143, 0.427, 0.429);
  private final Color kGreenTarget = ColorMatch.makeColor(0.197, 0.561, 0.240);
  private final Color kRedTarget = ColorMatch.makeColor(0.561, 0.232, 0.114);//switch this to be more accurate
  private final Color kYellowTarget = ColorMatch.makeColor(0.361, 0.524, 0.113);

  Joystick arcadeStick = new Joystick(0);
  TalonSRX WheelSpinner = new TalonSRX(5);
  double magScale = 1;//scaling factor to convert the raw encoder value to distance traveled
  double currentPos = 0;
  double startPos = 0;
  double targetPos = 0;

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

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */

   //----------------------------------------------uncomment this code below if you can in fact reset the encoder
/**
 * public void Spin (double position, double speed) {
    WheelSpinner.setSelectedSensorPosition(0);//possible way to reset the encoder
    currentPos = WheelSpinner.getSelectedSensorPosition();
    targetPos = position;
    while (currentPos < targetPos) {
      WheelSpinner.set(ControlMode.PercentOutput, speed);
      currentPos = WheelSpinner.getSelectedSensorPosition();
    }
    WheelSpinner.set(ControlMode.Current, 0);
  }
 */
  public void Spin (double position, double speed) {
    WheelSpinner.setSelectedSensorPosition(0);//possible way to reset the encoder
    startPos = WheelSpinner.getSelectedSensorPosition();
    currentPos = WheelSpinner.getSelectedSensorPosition();
    targetPos = startPos + position;
    while (currentPos < targetPos) {
      WheelSpinner.set(ControlMode.PercentOutput, speed);
      currentPos = WheelSpinner.getSelectedSensorPosition() - startPos;
    }
    WheelSpinner.set(ControlMode.PercentOutput, 0);
  }

  public void stop() {
    WheelSpinner.set(ControlMode.PercentOutput, 0);
  }

  public void Position() {
    Color detectedColor = m_colorSensor.getColor();
    /**
     * Run the color match algorithm on our detected color
     */
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

        if(gameData == null) {
          stop();
      } else if(gameData.charAt(0) == 'G' && colorString == "Red") {
        Spin(1, .5);
      } else if(gameData.charAt(0) == 'B' && colorString == "Red") {
        Spin(1, .5);
      } else if(gameData.charAt(0) == 'Y' && colorString == "Red") {
        Spin(1, .5);
      } else if(gameData.charAt(0) == 'R' && colorString == "Blue") {
        Spin(1, .5);
      } else if(gameData.charAt(0) == 'G' && colorString == "Blue") {
        Spin(1, .5);
      } else if(gameData.charAt(0) == 'Y' && colorString == "Blue") {
        Spin(1, .5);
      } else if(gameData.charAt(0) == 'R' && colorString == "Green") {
        Spin(1, .5);
      } else if(gameData.charAt(0) == 'B' && colorString == "Green") {
        Spin(1, .5);
      } else if(gameData.charAt(0) == 'Y' && colorString == "Green") {
        Spin(1, .5);
      } else if(gameData.charAt(0) == 'R' && colorString == "Yellow") {
        Spin(1, .5);
      } else if(gameData.charAt(0) == 'G' && colorString == "Yellow") {
        Spin(1, .5);
      } else if(gameData.charAt(0) == 'B' && colorString == "Yellow") {
        Spin(1, .5);
      } else {
        stop();
      }
  }


  @Override
  public void robotPeriodic() {
    /*
      while(true) {
    		if (Double.compare(gameDataTimer.get(), gameDataTimeOut) <= 0 && gameData == null) {
    			Timer.delay(0.02);
    		} else {
    			break;
    		}
    		gameData = DriverStation.getInstance().getGameSpecificMessage();
    		SmartDashboard.putString("Game Data", gameData);
    		gameData = ("NULL".equalsIgnoreCase(gameData)) ? null : gameData;
      }
      */  
    WheelSpinner.set(ControlMode.PercentOutput, arcadeStick.getY());
    /**
     * reset encoder or get the current encoder value
     * run the motor until the encoder count = desired distance
     * while currentpos < targetpos
     * run the motor
     */


    /*
    if (arcadeStick.getRawButton(1)) {
     Spin(10, .2);
    } else {
      stop();
    }
    if (arcadeStick.getRawButton(2)) {
      WheelSpinner.setSelectedSensorPosition(0);
    }
    if (arcadeStick.getRawButton(3)) {
    WheelSpinner.set(ControlMode.Position, arcadeStick.getY());
    }
*/
    Color detectedColor = m_colorSensor.getColor();
    /**
     * Run the color match algorithm on our detected color
     */
    
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
    /*
     * Open Smart Dashboard or Shuffleboard to see the color detected by the 
     * sensor.
     */
    SmartDashboard.putNumber("Red", detectedColor.red);
    SmartDashboard.putNumber("Green", detectedColor.green);
    SmartDashboard.putNumber("Blue", detectedColor.blue);
    SmartDashboard.putNumber("Confidence", match.confidence);
    SmartDashboard.putString("Detected Color", colorString);
    SmartDashboard.putNumber("Encoder Value", WheelSpinner.getSelectedSensorPosition());
    SmartDashboard.putNumber("Encoder Value", WheelSpinner.getSelectedSensorPosition() * magScale);//scaled to distance traveled
    SmartDashboard.putString("FMS Color", gameData);

  }
  

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
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
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
