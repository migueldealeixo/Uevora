% Predicado copy/0
copy :- 
    get_char(C), 
    ( C = end_of_file -> true ; put_char(C), copy ).

% Predicado to_lc/0
to_lc :- 
    get_char(C), 
    ( C = end_of_file -> true ; 
      ( char_type(C, upper(U)) -> char_type(L, lower(U)), put_char(L) ; put_char(C) ), 
      to_lc ).