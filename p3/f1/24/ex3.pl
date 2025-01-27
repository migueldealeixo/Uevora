impares([], []).                        
impares([X|T], [X|B]) :-                 
    impares_aux(T, B, 2).                
impares_aux([], [], _).                 
impares_aux([X|T], [X|B], N) :-         
    N mod 2 =:= 1,                       
    N1 is N + 1,                         
    impares_aux(T, B, N1).              

impares_aux([_|T], B, N) :-             
    N mod 2 =:= 0,                       
    N1 is N + 1,                        
    impares_aux(T, B, N1).               
