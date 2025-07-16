% Estado inicial
estado_inicial(estado([_,_,_,_,_,_,_,_,_], jogador1)).

% Acesso ao tabuleiro e jogador
estado_board(estado(Board, _), Board).
estado_jogador(estado(_, Jogador), Jogador).

% Terminal: jogo terminou se há SOS ou tabuleiro cheio
terminal(Estado) :-
    estado_board(Estado, Board),
    (tem_SOS(Board); tabuleiro_cheio(Board)).

% Tabuleiro cheio: não há casas vazias (i.e., sem variáveis)
tabuleiro_cheio(Board) :- \+ (member(X, Board), var(X)).

% Função de utilidade
utilidade(Estado, 1) :-
    estado_board(Estado, Board),
    tem_SOS(Board),
    estado_jogador(Estado, jogador1), !.

utilidade(Estado, -1) :-
    estado_board(Estado, Board),
    tem_SOS(Board),
    estado_jogador(Estado, jogador2), !.

utilidade(Estado, 0) :-
    estado_board(Estado, Board),
    tabuleiro_cheio(Board),
    \+ tem_SOS(Board), !.

% SOS nas linhas, colunas e diagonais
tem_SOS(Board) :-
    member(Line, [
        [1,2,3], [4,5,6], [7,8,9],       % Linhas
        [1,4,7], [2,5,8], [3,6,9],       % Colunas
        [1,5,9], [3,5,7]                 % Diagonais
    ]),
    get_trio(Board, Line, ['S','O','S']).

get_trio(Board, [A,B,C], ['S','O','S']) :-
    nth1(A, Board, SA), nonvar(SA), SA = 'S',
    nth1(B, Board, SB), nonvar(SB), SB = 'O',
    nth1(C, Board, SC), nonvar(SC), SC = 'S'.

% Jogador seguinte
outro_jogador(jogador1, jogador2).
outro_jogador(jogador2, jogador1).

% Jogadas possíveis
op1(estado(Board, Player), (Pos, Letter), estado(NewBoard, NextPlayer)) :-
    between(1, 9, Pos),
    nth1(Pos, Board, Cell),
    var(Cell),
    member(Letter, ['S', 'O']),
    set_nth1(Pos, Board, Letter, NewBoard),
    (tem_SOS(NewBoard) -> NextPlayer = Player ; outro_jogador(Player, NextPlayer)).

set_nth1(1, [_|T], X, [X|T]).
set_nth1(N, [H|T], X, [H|R]) :-
    N > 1,
    N1 is N - 1,
    set_nth1(N1, T, X, R).

% Escolhe jogada com maior valor
escolhe_max([V-Op|Vs], BestOp) :-
    escolhe_max(Vs, V-Op, BestOp).
escolhe_max([], _-BestOp, BestOp).
escolhe_max([V-Op|Vs], V1-BestOp1, BestOp) :-
    (V > V1 -> escolhe_max(Vs, V-Op, BestOp)
    ;         escolhe_max(Vs, V1-BestOp1, BestOp)).

% Minimax puro
minimax_decidir(Estado, MelhorJogada) :-
    findall(Valor-Jogada,
        (op1(Estado, Jogada, NovoEstado),
         minimax_valor(NovoEstado, Valor)),
        ListaValores),
    escolhe_max(ListaValores, MelhorJogada).

minimax_valor(Ei, Val) :-
    terminal(Ei), !,
    utilidade(Ei, Val).
minimax_valor(Ei, Val) :-
    findall(Es, op1(Ei, _, Es), L),
    maplist(minimax_valor, L, Vals),
    estado_jogador(Ei, Jogador),
    (Jogador == jogador1 -> maximo(Vals, Val) ; minimo(Vals, Val)).

maximo([V|Vs], Max) :- foldl(max_, Vs, V, Max).
max_(A, B, B) :- B > A, !.
max_(A, _, A).
minimo([V|Vs], Min) :- foldl(min_, Vs, V, Min).
min_(A, B, B) :- B < A, !.
min_(A, _, A).

% ALFA-BETA COM CONTADOR

% Interface
minimax_ab_valor_contador(Ei, Alpha, Beta, MelhorJogada, Valor, NosFinais) :-
    minimax_ab_valor(Ei, Alpha, Beta, MelhorJogada, Valor, 0, NosFinais).

% Terminal: soma 1 nó
minimax_ab_valor(Ei, _, _, _, Val, N, N1) :-
    terminal(Ei), !,
    utilidade(Ei, Val),
    N1 is N + 1.

% Jogadas possíveis + alternância max/min
minimax_ab_valor(Ei, Alpha, Beta, MelhorJogada, Valor, N, Nf) :-
    findall(Es-Op, op1(Ei, Op, Es), Moves),
    estado_jogador(Ei, Jogador),
    ( Jogador == jogador1 ->
        max_val(Moves, Alpha, Beta, (none, -1000), (MelhorJogada, Valor), N, Nf)
    ;   min_val(Moves, Alpha, Beta, (none, 1000), (MelhorJogada, Valor), N, Nf)
    ).

% MAX
max_val([], _, _, (Op, Val), (Op, Val), N, N).
max_val([E-Op|Moves], Alpha, Beta, (OpAt, ValAt), Best, N, Nf) :-
    minimax_ab_valor(E, Alpha, Beta, _, ValAtual, N, N1),
    ( ValAtual > ValAt ->
        NewAlpha = ValAtual,
        NewBest = (Op, ValAtual)
    ;   NewAlpha = Alpha,
        NewBest = (OpAt, ValAt)
    ),
    ( NewAlpha >= Beta ->
        Best = NewBest,
        Nf = N1
    ;   max_val(Moves, NewAlpha, Beta, NewBest, Best, N1, Nf)
    ).

% MIN
min_val([], _, _, (Op, Val), (Op, Val), N, N).
min_val([E-Op|Moves], Alpha, Beta, (OpAt, ValAt), Best, N, Nf) :-
    minimax_ab_valor(E, Alpha, Beta, _, ValAtual, N, N1),
    ( ValAtual < ValAt ->
        NewBeta = ValAtual,
        NewBest = (Op, ValAtual)
    ;   NewBeta = Beta,
        NewBest = (OpAt, ValAt)
    ),
    ( NewBeta =< Alpha ->
        Best = NewBest,
        Nf = N1
    ;   min_val(Moves, Alpha, NewBeta, NewBest, Best, N1, Nf)
    ).

% Executa e compara com contagem
comparar_ab_com_nos :-
    estado_inicial(Ei),
    statistics(runtime, [T0|_]),
    minimax_ab_valor_contador(Ei, -1000, 1000, MelhorJogada, Valor, Nos),
    statistics(runtime, [T1|_]),
    Tempo is T1 - T0,
    format('Melhor jogada: ~w com valor ~w~n', [MelhorJogada, Valor]),
    format('Tempo: ~d ms~n', [Tempo]),
    format('Nós explorados: ~d~n', [Nos]).


% Função de Heurisitica
avaliacao(Estado, Valor) :-
    estado_board(Estado, Board),
    findall(_, padrao_SO(Board), SOs),
    length(SOs, NumSO),
    findall(_, padrao_OS(Board), OSs),
    length(OSs, NumOS),
    Valor is NumSO + NumOS.


padrao_SO(Board) :-
    member([A,B], [[1,2],[2,3],[4,5],[5,6],[7,8],[8,9],[1,4],[4,7],[2,5],[5,8],[3,6],[6,9],[1,5],[5,9],[3,5],[5,7]]),
    nth1(A, Board, SA), nonvar(SA), SA = 'S',
    nth1(B, Board, SB), nonvar(SB), SB = 'O'.

padrao_OS(Board) :-
    member([A,B], [[1,2],[2,3],[4,5],[5,6],[7,8],[8,9],[1,4],[4,7],[2,5],[5,8],[3,6],[6,9],[1,5],[5,9],[3,5],[5,7]]),
    nth1(A, Board, SA), nonvar(SA), SA = 'O',
    nth1(B, Board, SB), nonvar(SB), SB = 'S'.

% Mostra o tabuleiro de forma amigável
display_board(Estado) :-
    estado_board(Estado, Board),
    format('~n'),
    display_row(Board, 1),
    write('-----------'), nl,
    display_row(Board, 4),
    write('-----------'), nl,
    display_row(Board, 7),
    nl.

display_row(Board, N) :-
    N1 is N, N2 is N+1, N3 is N+2,
    get_cell(Board, N1, C1),
    get_cell(Board, N2, C2),
    get_cell(Board, N3, C3),
    format(' ~w | ~w | ~w ~n', [C1, C2, C3]).

get_cell(Board, Pos, C) :-
    nth1(Pos, Board, Cell),
    (var(Cell) -> C = ' ' ; C = Cell).

% Loop principal do jogo
play_game :-
    estado_inicial(Estado),
    display_board(Estado),
    play_game(Estado).

play_game(Estado) :-
    terminal(Estado), !,
    utilidade(Estado, Val),
    (Val = 1 -> write('Jogador1 (Agent) venceu!'), nl
    ; Val = -1 -> write('Jogador2 (User) venceu!'), nl
    ; write('Empate!'), nl
    ).

play_game(Estado) :-
    estado_jogador(Estado, Jogador),
    (Jogador == jogador1 ->
        agent_turn(Estado, ProxEstado)
    ;
        user_turn(Estado, ProxEstado)
    ),
    display_board(ProxEstado),
    play_game(ProxEstado).

% Turno do agente (jogador1)
agent_turn(Estado, ProxEstado) :-
    write('Vez do Agent (Jogador1)...'), nl,
    minimax_decidir(Estado, (Pos, Letra)),
    op1(Estado, (Pos, Letra), ProxEstado),
    write('Agent jogou: Posição '), write(Pos), write(', Letra '), write(Letra), nl.

% Turno do usuário (jogador2)
user_turn(Estado, ProxEstado) :-
    write('Vez do User (Jogador2)...'), nl,
    repeat,
    write('Digite a posição (1-9): '), flush_output,
    read(Pos),
    write('Digite a letra (S ou O): '), flush_output,
    read(Letra),
    (op1(Estado, (Pos, Letra), ProxEstadoUser) ->
        ProxEstado = ProxEstadoUser
    ;   write('Jogada inválida. Tente novamente.'), nl,
        fail
    ).