/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team3206.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Robot extends IterativeRobot {
	Joystick PIDstick = new Joystick(0);
	VictorSP mleft = new VictorSP(0);
	VictorSP mright = new VictorSP(0);
	DifferentialDrive motors = new DifferentialDrive(mleft, mright);
	
	double P = .1;
	double I = .001;
	double D = 0;
	
	AnalogInput PIDin = new AnalogInput(0);
	PIDController drivetrainPID = new PIDController(P, I, D, PIDin, mright);	
	
	@Override
	public void robotInit() {
		
	}

	
	@Override
	public void autonomousInit() {

	}

	
	@Override
	public void autonomousPeriodic() {
		
		}
	

	
	@Override
	public void teleopPeriodic() {
		drivetrainPID.enable();

		while(isOperatorControl()) {
			drivetrainPID.setSetpoint(PIDstick.getY());
			Timer.delay(.02);
			
			SmartDashboard.putNumber("P = ", P);
			SmartDashboard.putNumber("I = ", I);
			SmartDashboard.putNumber("D = ", D);
		}
		
	}

	
	@Override
	public void testPeriodic() {
		
	}
}
