package com.example.prashanth.porterdemo.factory;

import com.example.prashanth.porterdemo.engine.Engine;
import com.example.prashanth.porterdemo.engine.IEngine;

public class EngineFactory {

        public static IEngine getEngine(){
            return Engine.getInstance();
        }
}
