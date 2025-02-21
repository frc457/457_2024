// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.MISC;
import frc.robot.Constants.OperatorConstants;
// import frc.robot.subsystems.ArmSubsystem;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.ShooterSubsystem;
import com.revrobotics.CANSparkMax;
import frc.robot.Constants;


/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a "declarative" paradigm, very
 * little robot logic should actually be handled in the {@link Robot} periodic methods (other than the scheduler calls).
 * Instead, the structure of the robot (including subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer
{
  // The robot's subsystems and commands are defined here...
  public static final DriveSubsystem   m_driveSubsystem   = new DriveSubsystem();
  // private final ArmSubsystem     m_armSubsystem     = new ArmSubsystem();
  private final IntakeSubsystem  m_intakeSubsystem  = new IntakeSubsystem();
  private final ShooterSubsystem m_shooterSubsystem = new ShooterSubsystem();

  // Replace with CommandPS4Controller or CommandJoystick if needed
  private final XboxController m_driverController   = new XboxController(OperatorConstants.kDriverControllerPort);
  private final XboxController m_operatorController = new XboxController(OperatorConstants.kOperatorControllerPort);

  private final double armManualSpeed = MISC.ARM_MANUAL_SPEED;

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer()
  {
    // try
    // {
    //   CameraServer.startAutomaticCapture();
    // } catch (Exception e)
    // {
    //   DriverStation.reportWarning(e.getMessage(), false);
    // }
    // Configure the trigger bindings
    configureBindings();
    NamedCommands.registerCommand("runShooter", runShooter(2100));
    NamedCommands.registerCommand("intakeFullSpeed", m_intakeSubsystem.intakeCommand(0.8));
    NamedCommands.registerCommand("stopShooter", m_shooterSubsystem.setShooterCmd(0));
    NamedCommands.registerCommand("stopIntake", m_intakeSubsystem.setIntakeCmd(0));
    m_driveSubsystem.setupPathPlanner();
  }

  /**
   * Run the shooter at the RPM and feed when up to speed.
   *
   * @param velocityRPM Velocity of the shooter as RPM.
   * @return {@link ParallelCommandGroup} which runs the shooter than intake when ready.
   */
  public Command runShooter(double velocityRPM)
  {
    return new ParallelCommandGroup(m_shooterSubsystem.runShooterCmd(velocityRPM),
                                    m_shooterSubsystem.waitForShooter()
                                                      .andThen(m_intakeSubsystem.feedCommand(-0.3)));
  }

  /**
   * Use this method to define your trigger->command mappings. Triggers can be created via the
   * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with an arbitrary predicate, or via the
   * named factories in {@link edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for
   * {@link CommandXboxController Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller PS4}
   * controllers or {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight joysticks}.
   */
  private void configureBindings()
  {
    m_driveSubsystem.setDefaultCommand(m_driveSubsystem.driveCommand(m_driverController::getLeftY,
                                                                     m_driverController::getRightY));
    m_intakeSubsystem.setDefaultCommand(m_intakeSubsystem.setIntakeCmd(0));
    m_shooterSubsystem.setDefaultCommand(m_shooterSubsystem.setShooterCmd(0));


    // Arm Preset Commands
    // new Trigger(m_operatorController::getAButton).whileTrue(m_armSubsystem.setArmLocation(0));
    // new Trigger(m_operatorController::getBButton).whileTrue(m_armSubsystem.setArmWithSpeedControl(80, 0.03, 0.01));

    // Manual Arm Movement Commands
    // m_armSubsystem.setDefaultCommand(m_armSubsystem.setManualArmLocation(m_operatorController::getRightY));

    // new Trigger(m_operatorController::getXButton).whileTrue(m_armSubsystem.setManualArmLocation("down", armManualSpeed));
    // new Trigger(m_operatorController::getYButton).whileTrue(m_armSubsystem.setManualArmLocation("up", armManualSpeed));

    // new Trigger(m_operatorController::getYButton).whileTrue(m_shooterSubsystem.setShooterCmd(0.420));
    new Trigger(m_operatorController::getLeftBumper).whileTrue(m_intakeSubsystem.intakeCommand(.6));
    new Trigger(m_operatorController::getRightBumper).whileTrue(m_intakeSubsystem.intakeCommand(-0.35));

  }

  /**
   * 0 Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand()
  {

    // An example command will be run in autonomous
    return AutoBuilder.buildAuto("auto"); //Autos.exampleAuto(m_driveSubsystem);
  }
}
