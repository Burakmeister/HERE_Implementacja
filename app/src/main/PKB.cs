using System;
using System.Collections.Generic;

public class PKB
{
    private static PKB instance;
    private int[] varTable;
    private Statement[] procTable;

    private Dictionary<Statement, Statement> followsTable;
    private Dictionary<Statement, Statement> parentTable;
    private Dictionary<Statement, HashSet<string>> usesTable;
    private Dictionary<Statement, HashSet<string>> modifiesTable;

    // konstruktor zapobiegający kolejnym instancjom
    public PKB()
    {
        varTable = new int[100];
        procTable = new Statement[50]; 
        followsTable = new Dictionary<Statement, Statement>();
        parentTable = new Dictionary<Statement, Statement>();
        usesTable = new Dictionary<Statement, HashSet<string>>();
        modifiesTable = new Dictionary<Statement, HashSet<string>>();
    }

    public static PKB GetInstance()
    {
        if (instance == null)
        {
            instance = new PKB();
        }
        return instance;
    } 
}