package killergame;

public class KillerRules {
    
    public KillerRules(){
        
    }
    // 1: explotan los 2
    // 2: el objeto A rebota
    // 3: solo muere A
    public int crashed(VisibleObject a, VisibleObject b){
        
        if(b instanceof Static){
            if(a instanceof Autonomous){
                return 2;
            }
            if(a instanceof Controlled){
                return 3;
            }            
        }
        return 1;
    }
    
}
