package Domain;

public enum DiaSetmana {
    DILLUNS,
    DIMARTS,
    DIMECRES,
    DIJOUS,
    DIVENDRES,
    DISSABTE,
    DIUMENGE;

    public int num(){
        switch(this) {
            case DILLUNS:
                return 0;
            case DIMARTS:
                return 1;
            case DIMECRES:
                return 2;
            case DIJOUS:
                return 3;
            case DIVENDRES:
                return 4;
            case DISSABTE:
                return 5;
            case DIUMENGE:
                return 6;
            default: throw new IllegalStateException();
        }
    }

    public static DiaSetmana nom(int in){
        switch(in) {
            case 0:
                return DILLUNS;
            case 1:
                return DIMARTS;
            case 2:
                return DIMECRES;
            case 3:
                return DIJOUS;
            case 4:
                return DIVENDRES;
            case 5:
                return DISSABTE;
            case 6:
                return DIUMENGE;
            default: throw new IllegalStateException();
        }
    }
}
