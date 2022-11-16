package com.epam.training.ticketservice.ui.shell.command;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.commands.Quit;

@ShellComponent
public class SystemCommand implements Quit.Command {
    @ShellMethod(key = "exit", value = "Terminates the application process.")
    public void exit() { System.exit(0); }
}
