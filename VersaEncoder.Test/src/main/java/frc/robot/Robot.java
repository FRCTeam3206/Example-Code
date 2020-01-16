/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
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

  TalonSRX WheelSpinner = new TalonSRX(5);
  Joystick arcadeStick = new Joystick(0);

  double currentPos = 0;
  double startPos = 0;
  double targetPos = 0;
  double rpmSetpoint = 1000;
  double magScale = .00025553;//(1/4096 * 3.14 * 4)/12;//scaling factor to convert the raw encoder value to distance traveled
  double desiredDistance;
  double distanceTraveled = 0;
  double motorSpeed = .8; //.85 max
  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
    WheelSpinner.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
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

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    WheelSpinner.set(ControlMode.PercentOutput, arcadeStick.getY());
    SmartDashboard.putNumber("Encoder Value", WheelSpinner.getSelectedSensorPosition() * magScale);
    SmartDashboard.putNumber("Distance Traveled", distanceTraveled); 
    SmartDashboard.putNumber("RPM/Velocity", WheelSpinner.getSelectedSensorVelocity());
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
    //spin the wheel 3 times(color wheel thing)
    if (arcadeStick.getRawButton(1)) {
      Spin(26); //24ft is 3 rotations around the color wheel
  } else {
    WheelSpinner.set(ControlMode.PercentOutput, 0);
  }

  //Set RPM/Velocity of the wheels to be static. build in failsafe later
  if (arcadeStick.getRawButton(2)) {
    WheelSpinner.set(ControlMode.Velocity, rpmSetpoint);
  }
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
