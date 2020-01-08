/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;


public class Robot extends TimedRobot {

  Joystick leftstick = new Joystick(0);
  Joystick rightstick = new Joystick(1);

  VictorSP leftmotor = new VictorSP(0);
  VictorSP rightmotor = new VictorSP(1);

  DifferentialDrive robotdrive = new DifferentialDrive(leftmotor, rightmotor);

  @Override
  public void robotInit() {
  
  }

  
  @Override
  public void robotPeriodic() {
    robotdrive.tankDrive(leftstick.getY() * .6, rightstick.getY() * .6);
  }

  
  @Override
  public void autonomousInit() {
   
  }

 
  @Override
  public void autonomousPeriodic() {
    
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
