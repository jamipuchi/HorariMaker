# HorariMaker
En aquest projecte jo, l'Arnau Buch i en Miquel Prieto hem fet un programa per generar horaris.

## Funcionalidats
La nostra aplicació és una eina per generar horaris per tot tipus d’institucions educatives.
Al obrir-la l’usuari podrà crear una institució o sel·leccionar la seva, la qual el programa carregarà des de la “base de dades”.
Un cop fet això haurà d’entrar a la seva sessió, si existeix, dins de la institució. Només el creador de la institució podrà crear nous usuaris i assignar permisos (creació d’usuaris, creació d’assignatures,...).
Dins de la sessió l’usuari podrà començar a generar horaris.
Per poder generar un horari calen certs paràmetres definits per l’usuari. Aquests inclouen aules (amb la seva capacitat), assignatures (amb les seves dades com ara capacitat, grups i subgrups), pla d’estudis o relacions de requisit entre assignatures, entre d’altres.
Amb totes aquestes dades el programa ja serà capaç de generar el millor horari disponible o, en el cas que no existeixi tal horari, avisar l’usuari que modifiqui la configuració.
Per cada horari generat, l’usuari podrà guardar-lo al programa (quedant associat al seu usuari).

Restriccions del sistema de generació d’horaris que conté el sistema:

- Hora inici i fi de classes (marc horari) per cada dia

- Dies en que es fa classe (dilluns a dissabte)

- No hores solapades entre assignatures del mateix nivell (corequisits)

- Cert dia començar tard o cert dia acabar aviat (entra dins marc horari) 

- Indeterminada: en certes hores no es pot fer classe (serveix per àpats, pauses, examens...) 

- Límit d’hores per matèria 

- Grups/classes: per exemple, el grup o classe només pot fer classe al matí o a la tarda 

