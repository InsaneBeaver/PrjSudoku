/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prjsudoku;
import java.util.Random;
/**
 *
 * @author 11110305
 */


class Noeud
{
    public Noeud(int _x, int _y) 
    {
        x = _x;
        y = _y;
        valeursPossiblesStatiques = new boolean[9];
        noeudsRelies = new Noeud[27];
        
        for(int i = 0; i < 9; i++)
            valeursPossiblesStatiques[i] = true;
        
        prochainNoeud = null;
    }
    Noeud prochainNoeud;
    boolean[] valeursPossiblesStatiques;
    Noeud[] noeudsRelies;
    int noeudsReliesIndex = 0;
    public int valeur;
    public int x; 
    public int y;
    
    // Pré résolution
    private boolean pointEstUtile(int x2, int y2)
    {
       
       boolean estMemeRangee = y == y2;
       boolean estMemeColonne = x == x2;
       boolean estMemeCarre = (x - x % 3) == (x2 - x2 % 3) && (y - y % 3) == (y2 - y2 % 3);
       
       return estMemeRangee || estMemeColonne || estMemeCarre;
       
        
    }
    
    public void donnerPoint(int x2, int y2, int valeur)
    {
        if(pointEstUtile(x2, y2))
            valeursPossiblesStatiques[valeur - 1] = false;
    }
    
    public void donnerNoeud(int x2, int y2, Noeud noeud)
    {
        if(pointEstUtile(x2, y2))
        {
            noeudsRelies[noeudsReliesIndex] = noeud;
            noeudsReliesIndex++;
        }
    }
    
    public void donnerProchainNoeud(Noeud noeud)
    {
        prochainNoeud = noeud;
    }
    
    // Pendant la résolution
    public boolean resoudre()
    {
        boolean[] valeursPossiblesDynamiques = new boolean[9];
        for(int i = 0; i < 9; i++)
            valeursPossiblesDynamiques[i] = true;
        
        
        for(Noeud noeudRelie:noeudsRelies)
        {
            if(noeudRelie != null)
                valeursPossiblesDynamiques[noeudRelie.valeur - 1] = false;
        }
        
        boolean ok = false;
        for(int i = 0; i < 9; i++)
        {
            if(valeursPossiblesStatiques[i] && valeursPossiblesDynamiques[i])
            {
                //System.out.println("Candidat: " + (i + 1));
                valeur = i + 1;
                /*
                if(prochainNoeud == null)
                    System.out.println("SOLUTION!!!!!");
                */
                
                if(prochainNoeud == null || prochainNoeud.resoudre())
                {
                    ok = true;
                    break;
                }
            }
        }
        if(!ok)
        {
            valeur = 0;
        }
        
        return ok;
        
    }
}

public class PrjSudoku {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        double temps = 0;
        for(int i = 0; i < 1000000; i++)
        {
            temps += test();
            System.out.println(temps / (i + 1));
        }
        
        System.out.println(temps / 1000000);
    }
    public static double test()
    {
        
        int[][] cases = {
             {0, 0, 6, 0, 1, 0, 2, 0, 0},
             {2, 0, 9, 0, 7, 0, 0, 4, 0},
             {0, 7, 1, 0, 0, 5, 0, 6, 0},
             {0, 0, 4, 0, 6, 0, 7, 8, 0},
             {1, 0, 2, 7, 0, 8, 4, 0, 5},
             {0, 8, 5, 0, 4, 0, 3, 0, 0},
             {0, 2, 0, 6, 0, 0, 9, 3, 0},
             {0, 9, 0, 0, 8, 0, 5, 0, 4},
             {0, 0, 7, 0, 9, 0, 6, 0, 0}
             
        };
        /*
        for(int x = 0; x < 9; x++)
        {
            for(int y = 0; y < 9; y++)
            {
                System.out.print(cases[x][y] + " ");
            }
            System.out.println("");
        }
        System.out.println("==============================================");
        
        */
     
        Noeud[] noeudsCrees = new Noeud[81];
        int indexNoeud = 0;
        for(int x = 0; x < 9; x++)
            for(int y = 0; y < 9; y++)
            {
                if(cases[x][y] == 0)
                {
                    Noeud nouveauNoeud = new Noeud(x, y);
                    
                    // Test des points
                    for(int x2 = 0; x2 < 9; x2++)
                        for(int y2 = 0; y2 < 9; y2++)
                        {
                            if(cases[x2][y2] != 0)
                                nouveauNoeud.donnerPoint(x2, y2, cases[x2][y2]);
                        }
                    
                    noeudsCrees[indexNoeud] = nouveauNoeud;
                    indexNoeud++;
                }
                
                
            }
        
        
        
        Random foo = new Random();
        for(int i = 0; i < 100000; i++)
        {
            int pos1 = foo.nextInt(indexNoeud);
            int pos2 = foo.nextInt(indexNoeud);
            
            Noeud val = noeudsCrees[pos1];
            noeudsCrees[pos1] = noeudsCrees[pos2];
            noeudsCrees[pos2] = val;
      
        }
       
        // Construction du réseau
        for(int i = 0; i < indexNoeud; i++)
        {
                                
            for(int j = 0; j < i; j++)
            {
                Noeud noeud = noeudsCrees[j];
                noeudsCrees[i].donnerNoeud(noeud.x, noeud.y, noeud);
            }
            noeudsCrees[i].donnerProchainNoeud(noeudsCrees[i+1]);
        }
        
        noeudsCrees[indexNoeud - 1].donnerProchainNoeud(null);
        
        double debut = System.nanoTime();   
        noeudsCrees[0].resoudre();
       
        // Intégration des valeurs obtenues dans la grille
        for(int i = 0; i < indexNoeud; i++)
        {
            Noeud noeud = noeudsCrees[i];
            cases[noeud.x][noeud.y] = noeud.valeur;
        }
        
        /*
        for(int x = 0; x < 9; x++)
        {
            for(int y = 0; y < 9; y++)
            {
                System.out.print(cases[x][y] + " ");
            }
            System.out.println("");
        }
*/
        return ((System.nanoTime() - debut) / 1e09);
    }
    
}
