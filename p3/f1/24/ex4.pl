repetidos([], []).

repetidos([X|T], [X|R]) :-
    conta_o(X, [X|T], N),
    N > 1,
    remove_elemento(X, T, T1),
    repetidos(T1, R).

repetidos([X|T], R) :-
    conta_o(X, [X|T], N),
    N =< 1,
    repetidos(T, R).

conta_o(_, [], 0).

conta_o(X, [X|T], N) :-
    conta_o(X, T, N1),
    N is N1 + 1.

conta_o(X, [Y|T], N) :-
    X \= Y,
    conta_o(X, T, N).

remove_elemento(_, [], []).

remove_elemento(X, [X|T], T).

remove_elemento(X, [Y|T], [Y|Y1]) :-
    X \= Y,
    remove_elemento(X, T, Y1).
