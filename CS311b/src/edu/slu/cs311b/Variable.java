/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.slu.cs311b;

import java.util.HashMap;

/**
 *
 * @author d524lab
 */
public class Variable {

    public final static HashMap<String, Variable> symbolTable = new HashMap();

    String name;
    String type;
    Object value;
    int scope = -1;
    int lifetime = -1;

    public Variable(String name, String type, int scopeLineNo) {
        this.name = name;
        this.type = type;
        this.scope = scopeLineNo;

        symbolTable.put(this.name, this);
    }

    public void setLifetime(int lifetimeLineNo) {
        if (this.lifetime == -1) {
            this.lifetime = lifetimeLineNo;
        }
    }

    @Override
    public String toString() {
        return "{" + name + "," + type + "," + value + ")";
    }
}
