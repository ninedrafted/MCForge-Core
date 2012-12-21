/*******************************************************************************
 * Copyright (c) 2012 MCForge.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package net.mcforge.world;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.mcforge.API.level.LevelLoadEvent;
import net.mcforge.API.level.LevelPreLoadEvent;
import net.mcforge.API.level.LevelUnloadEvent;
import net.mcforge.iomodel.Player;
import net.mcforge.server.Server;
import net.mcforge.server.Tick;
import net.mcforge.world.generator.FlatGrass;

public class LevelHandler {

    private List<Level> levels = new CopyOnWriteArrayList<Level>();
    
    private Server server;
    
    /**
     * The constructor for a new level handler
     * @param server
     *              The server that requires a level handler
     */
    public LevelHandler(Server server) {
        this.server = server;
        server.Add(new Saver());
    }
    
    /**
     * Get a list of levels
     * @return
     *        A list of levels
     */
    public final List<Level> getLevelList() {
        return levels;
    }

    /**
     * Create a new level
     * @param name
     *            The name of the level
     * @param width
     *             The width (Max X)
     * @param height
     *              The height (Max Y)
     * @param depth
     *              The depth (Max Z) 
     */
    public void newLevel(String name, short width, short height, short length)
    {
        newLevel(name, width, height, length, new FlatGrass(server));
    }
    
    public void newLevel(String name, short width, short height, short length, Generator gen) {
        if(!new File("levels/" + name + ".ggs").exists())
        {
            Level level = new Level(width, height, length);
            level.name = name;
            level.generateWorld(gen);
            try {
                level.save();
            } catch (IOException e) {
                e.printStackTrace(server.getLoggerOutput());
            }
        }
    }
    
    /**
     * Find a level with the given name.
     * If part of a name is given, then it will try to find the
     * full name
     * @param name
     *            The name of the level
     * @return
     *         The level found. If more than 1 level is found, then
     *         it will return null
     */
    public Level findLevel(String name) {
        Level temp = null;
        for (int i = 0; i < levels.size(); i++) {
            if ((levels.get(i).name).equalsIgnoreCase(name))
                return levels.get(i);
            if ((levels.get(i).name).contains(name) && temp == null)
                temp = levels.get(i);
            else if ((levels.get(i).name).contains(name) && temp != null)
                return null;
        }
        return temp;
    }
    
    /**
     * Get the players in a particular level
     * @param level
     *             The level to check
     * @return
     *        A list of players in that level.
     */
    public ArrayList<Player> getPlayers(Level level) {
        ArrayList<Player> temp = new ArrayList<Player>();
        for (int i = 0; i < server.getPlayers().size(); i++)
            if (server.getPlayers().get(i).getLevel() == level)
                temp.add(server.getPlayers().get(i));
        return temp;
    }
    /**
     * Load all the levels in the 
     * "levels" folder
     */
    public void loadLevels()
    {
        levels.clear();
        File levelsFolder = new File("levels");
        File[] levelFiles = levelsFolder.listFiles();
        for(File f : levelFiles) {
            if (f.getName().endsWith(".ggs") || f.getName().endsWith(".lvl") || f.getName().endsWith(".dat"))
                loadLevel(levelsFolder.getPath() + "/" + f.getName());
        }
    }
    
    /**
     * Load a level and have it return the loaded
     * level
     * @param filename
     *                The .ggs file to load.
     *                If a .dat file is presented, then it will
     *                be converted to a .ggs
     * @return
     *         The loaded level.
     */
    public Level loadLevel(String filename) {
        Level l = null;
        LevelPreLoadEvent event1 = new LevelPreLoadEvent(filename);
        server.getEventSystem().callEvent(event1);
        if (event1.isCancelled()) {
            if ((l = event1.getReplacement()) == null)
                return null;
            else {
                levels.add(l);
                return l;
            }
        }
        try {
            long startTime = System.nanoTime();
            l = Level.Load(filename, server);
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            server.Log("Loading took: " + duration + "ms");
            LevelLoadEvent event = new LevelLoadEvent(l);
            server.getEventSystem().callEvent(event);
            if(event.isCancelled()) {
                server.Log("Loading of level " + l.name + " was canceled by " + event.getCanceler());
                l.unload(server); //Dispose the level
                l = null;
                return null;
            }
        } catch (ClassNotFoundException e) {
            server.Log("ERROR LOADING LEVEL!");
            e.printStackTrace();
        } catch (IOException e) {
            server.Log("ERROR LOADING LEVEL!");
            e.printStackTrace();
        }
        if (l != null) {
            server.Log("[" + l.name + "] Loaded!");
            levels.add(l);
        }
        return l;
    }
    /**
     * Unload a level
     * This method will call {@link Level#Unload(Server, boolean)} with save
     * as <b>true</b>.
     * @param level
     *             The level will unload
     * @return boolean
     *                Returns true if the level was unloaded, otherwise returns false.
     */
    public boolean unloadLevel(Level level) {
        return unloadLevel(level, true);
    }
    /**
     * Unload a level
     * @param level
     *             The level to unload
     * @param save
     *            Weather the level should save before unloading
     * 
     * @return boolean
     *                Returns true if the level was unloaded, otherwise returns false.
     */
    public boolean unloadLevel(Level level, boolean save) {
        LevelUnloadEvent event = new LevelUnloadEvent(level);
        server.getEventSystem().callEvent(event);
        if (event.isCancelled()) {
            server.Log("The unloading of level " + level + " was canceled by " + event.getCanceler());
            return false;
        }
        if (!levels.contains(level))
            return false;
        try {
            level.unload(server, save);
        } catch (IOException e) {
            e.printStackTrace(server.getLoggerOutput());
        }
        levels.remove(level);
        return true;
    }
    
    private class Saver implements Tick {
        int temp = 6000;
        @Override
        public void tick() {
            if (temp > 0) {
                temp--;
                return;
            }
            temp = 6000;
            for (int i = 0; i < levels.size(); i++) {
                if (levels.get(i).isAutoSaveEnabled()) {
                    try {
                        levels.get(i).save();
                    } catch (IOException e) {
                        e.printStackTrace(server.getLoggerOutput());
                    }
                }
            }
        }
    }

}
