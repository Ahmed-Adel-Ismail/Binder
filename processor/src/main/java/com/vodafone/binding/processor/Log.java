package com.vodafone.binding.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;


class Log {

    private static ProcessingEnvironment environment;

    static void setEnvironment(ProcessingEnvironment environment) {
        Log.environment = environment;
    }

    static void note(Object object){
        environment.getMessager().printMessage(Diagnostic.Kind.NOTE, String.valueOf(object));
    }


    static void error(Object object){
        environment.getMessager().printMessage(Diagnostic.Kind.ERROR, String.valueOf(object));
    }

    static void warning(Object object){
        environment.getMessager().printMessage(Diagnostic.Kind.WARNING, String.valueOf(object));
    }

}
