/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Joystick;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

  CANSparkMax topLeftMax = new CANSparkMax(2,MotorType.kBrushless);
  CANSparkMax topRightMax = new CANSparkMax(4,MotorType.kBrushless);
  
  CANSparkMax bottomLeftMax = new CANSparkMax(1,MotorType.kBrushless);
  CANSparkMax bottomRightMax = new CANSparkMax(3,MotorType.kBrushless);
  
  DifferentialDrive NeoDrive = new DifferentialDrive(topLeftMax, topRightMax);

  Joystick arcadeStick = new Joystick(0);

  //Limelight
  NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
  NetworkTableEntry tx = table.getEntry("tx");
  NetworkTableEntry ty = table.getEntry("ty");
  NetworkTableEntry ta = table.getEntry("ta");
  NetworkTableEntry tv = table.getEntry("tv");
  boolean LimelightHasValidTarget = false;
  double LimelightDriveCommand = 0;
  double LimelightSteerCommand = 0;

  //PID
  double kP = .04;
  double kI = .0004;
  double kD = 0;
  CANPIDController leftPID = new CANPIDController(topLeftMax);
  CANPIDController rightPID = new CANPIDController(topRightMax);
  boolean runDriveTrain = false;
  double integral, previous_error, setpoint = 0;
  double error;
  double derivative;
  double rcw;

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {

    bottomLeftMax.follow(topLeftMax);
    bottomRightMax.follow(topRightMax);

    topLeftMax.setInverted(false);
    topRightMax.setInverted(false);
    SmartDashboard.putNumber("kP", kP);
    SmartDashboard.putNumber("kI", kI);
    SmartDashboard.putNumber("kD", kD);
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
    /*

    if (arcadeStick.getRawButton(4)) {
      runDriveTrain = true;
    } else {
      runDriveTrain = false;
    } 
    while (runDriveTrain == true) {

    double x = tx.getDouble(0);
    double y = ty.getDouble(0);
    double area = ta.getDouble(0);
    double target = tv.getDouble(0);

    SmartDashboard.putNumber("X", x);
    SmartDashboard.putNumber("Y", y);
    SmartDashboard.putNumber("Area", area);
    SmartDashboard.putNumber("tv", target);

    double p = SmartDashboard.getNumber("kP", 0);
    double i = SmartDashboard.getNumber("kI", 0);
    double d = SmartDashboard.getNumber("kD", 0);

    setpoint = SmartDashboard.getNumber("SetPoint", 0);

    if (p != kP) { leftPID.setP(p, 0); kP = p;}
    if (i != kI) { leftPID.setI(i, 0); kI = i;}
    if (d != kD) { leftPID.setD(d, 0); kD = d;}

    if (p != kP) { rightPID.setP(p, 0); kP = p;}
    if (i != kI) { rightPID.setI(i, 0); kI = i;}
    if (d != kD) { rightPID.setD(d, 0); kD = d;}

    if (x < 0) { 
      NeoDrive.tankDrive(-.6, .6);
    } else if (x > 0) {
      NeoDrive.tankDrive(.6, -.6);
    }

    if (arcadeStick.getRawButton(1)) {
      runDriveTrain = false;
    }
  }
  NeoDrive.tankDrive(0, 0);
*/
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

    bottomLeftMax.follow(topLeftMax);
    bottomRightMax.follow(topRightMax);
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
    if (arcadeStick.getRawButton(4)) {
      runDriveTrain = true;
    } else {
      runDriveTrain = false;
    } 
        double x = tx.getDouble(0);

    SmartDashboard.putNumber("X", x);

    while (runDriveTrain == true) {
    //PID();
    NeoDrive.arcadeDrive(0 , PID());
    SmartDashboard.putNumber("output value", rcw);

    double p = SmartDashboard.getNumber("kP", 0);
    double i = SmartDashboard.getNumber("kI", 0);
    double d = SmartDashboard.getNumber("kD", 0);
    SmartDashboard.putNumber("X", x);


    if (p != kP) { kP = p;}
    if (i != kI) { kI = i;}
    if (d != kD) { kD = d;}

    if (arcadeStick.getRawButton(2)) {
      runDriveTrain = false;
    }
  }
  NeoDrive.arcadeDrive(0, arcadeStick.getX());
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }

  public double PID() {
    double x = tx.getDouble(0);
    error = setpoint - x;
    integral = integral + (error * .02);
    derivative = (error - previous_error) / .02;
    rcw = kP * error + kI * integral + kD * derivative;
    previous_error = error;
    return rcw;
  }
}
