/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import com.analog.adis16448.frc.ADIS16448_IMU;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */

public class Robot extends TimedRobot {
  public static final ADIS16448_IMU imu = new ADIS16448_IMU();

  double GyroAngle;
	double GyroCoefficient = 1.0; 
	int DesiredAngle;
  String DesiredDirection;

  private static final String RightBox  = "Right Box";
  private static final String LeftBox = "Left Box";
  private static final String TurnLeft = "Turn Left";
  private static final String TurnRight = "Turn Right";

  private SendableChooser<String> m_chooser = new SendableChooser<>();
  private String m_autoSelected;
  
  int i = 1;

  //Joystick leftStick = new Joystick(0);
  //Joystick rightStick = new Joystick(1);
  Joystick arcadeStick = new Joystick(0);

  //Left Drivetrain Motors
  WPI_TalonSRX leftTalon = new WPI_TalonSRX(5);
  WPI_VictorSPX leftSlave = new WPI_VictorSPX(3);

//Right Drivetrain Motors
  WPI_TalonSRX rightTalon = new WPI_TalonSRX(4);
  WPI_VictorSPX rightSlave = new WPI_VictorSPX(7);
  
  DifferentialDrive apolloDrive = new DifferentialDrive(leftTalon, rightTalon);
  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.addOption("Right Box", RightBox);
    m_chooser.addOption("Left Box", LeftBox);
    m_chooser.addOption("Turn Left", TurnLeft);
    m_chooser.addOption("Turn Right", TurnRight);
    SmartDashboard.putData("Turn Direction", m_chooser);

    leftTalon.configFactoryDefault();
      rightTalon.configFactoryDefault();
      leftSlave.configFactoryDefault();
      rightSlave.configFactoryDefault();

      leftTalon.setInverted(true);
      rightTalon.setInverted(true);
      leftSlave.setInverted(InvertType.FollowMaster);
      rightSlave.setInverted(InvertType.FollowMaster);

  }
  
  public void Turn(String Direction, int Angle) {

  GyroAngle = Math.abs(GyroCoefficient * imu.getAngleX());
  
  DesiredDirection = Direction;
  DesiredAngle = Angle;
  imu.reset();
  while( Math.abs(GyroAngle) <= DesiredAngle) {

    GyroAngle = Math.abs(GyroCoefficient * imu.getAngleX());

  if (DesiredDirection == "left") {
    apolloDrive.tankDrive(.6, -.6);
    imu.getAngleX();
    SmartDashboard.putNumber("Gyro Angle Degrees", GyroAngle);
  }
  else if(DesiredDirection == "right") {
  apolloDrive.tankDrive(-.6, .6);
  imu.getAngleX();
  SmartDashboard.putNumber("Gyro Angle Degrees", GyroAngle);
  }
  imu.getAngleX();
  SmartDashboard.putNumber("Gyro Angle Degrees", GyroAngle);
}
 imu.reset();
 apolloDrive.arcadeDrive(0, 0); 
}
 
  @Override
  public void robotPeriodic() {
    //apolloDrive.tankDrive(leftStick.getY(), rightStick.getY());
      apolloDrive.tankDrive(arcadeStick.getY() * .8, arcadeStick.getRawAxis(3) * .8);

    
    SmartDashboard.putNumber("Gyro-X", imu.getAngleX());
    //SmartDashboard.putNumber("Gyro-Y", imu.getAngleY());
    //SmartDashboard.putNumber("Gyro-Z",  Math.abs(imu.getAngleZ()));
    
    SmartDashboard.putNumber("Accel-X", imu.getAccelX());
    //SmartDashboard.putNumber("Accel-Y", imu.getAccelY());
    //SmartDashboard.putNumber("Accel-Z", imu.getAccelZ());
    /*
    SmartDashboard.putNumber("Pitch", imu.getPitch());
    SmartDashboard.putNumber("Roll", imu.getRoll());
    SmartDashboard.putNumber("Yaw", imu.getYaw());
    
    SmartDashboard.putNumber("Pressure: ", imu.getBarometricPressure());
    SmartDashboard.putNumber("Temperature: ", imu.getTemperature()); 
    */
    if (arcadeStick.getRawButton(2)) {
      imu.reset();
    } else if (arcadeStick.getRawButton(1)) {
      Turn("left", 90);
    } else if (arcadeStick.getRawButton(3)) {
      Turn("right", 90);
    }

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
    SmartDashboard.putString("Auto Selected: ", m_autoSelected);
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
  
    switch (m_autoSelected) {
      case LeftBox:
        for(i=1; i<=4; i++) {
          apolloDrive.arcadeDrive(.5, 0);
          Timer.delay(2);
          Turn("left", 90);
      }
      case RightBox:
        for(i=1; i<=4; i++) {
          apolloDrive.arcadeDrive(.5, 0);
          Timer.delay(2);
          Turn("right", 90);
      }
      case TurnLeft:
       Turn("left", 90);
       
      case TurnRight:
       Turn("right", 90);
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
    //apolloDrive.tankDrive(leftStick.getY(), rightStick.getY());
    apolloDrive.tankDrive(arcadeStick.getY() * .6, arcadeStick.getRawAxis(3) * .6);

    SmartDashboard.putNumber("Gyro-X", imu.getAngleX());
    SmartDashboard.putNumber("Gyro-Y", imu.getAngleY());
    SmartDashboard.putNumber("Gyro-Z",  Math.abs(imu.getAngleZ()));
    
    SmartDashboard.putNumber("Accel-X", imu.getAccelX());
    SmartDashboard.putNumber("Accel-Y", imu.getAccelY());
    SmartDashboard.putNumber("Accel-Z", imu.getAccelZ());
    
    SmartDashboard.putNumber("Pitch", imu.getPitch());
    SmartDashboard.putNumber("Roll", imu.getRoll());
    SmartDashboard.putNumber("Yaw", imu.getYaw());
    
    SmartDashboard.putNumber("Pressure: ", imu.getBarometricPressure());
    SmartDashboard.putNumber("Temperature: ", imu.getTemperature()); 

    
  }
}