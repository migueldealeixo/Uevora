% Passos
passo(s0, start).
passo(s1, mover(1,1,1,2)).
passo(s2, mover(1,2,2,2)).
passo(s3, mover(2,2,2,3)).
passo(s4, agarrar(5,2,3)).
passo(s5, mover(2,3,2,2)).
passo(s6, largar(5,2,2)).
passo(s7, mover(2,2,2,3)).
passo(s8, mover(2,3,3,3)).
passo(s9, agarrar(8,3,3)).
passo(s10, mover(3,3,2,3)).
passo(s11, largar(8,2,3)).
passo(s12, mover(2,3,3,3)).
passo(sf, goal).

% Movimentos
link(s0, posicao_robot(1,1), s1).
link(s0, adjacente(1,1,1,2), s1).
link(s1, posicao_robot(1,2), s2).
link(s1, adjacente(1,2,2,2), s2).
link(s2, posicao_robot(2,2), s3).
link(s2, adjacente(2,2,2,3), s3).
link(s3, posicao_robot(2,3), s4).
link(s3, posicao_robot(2,3), s5).
link(s0, adjacente(2,3,2,2), s5). 
link(s5, posicao_robot(2,2), s6).
link(s6, posicao_robot(2,2), s7).
link(s6, adjacente(2,2,2,3), s7).
link(s7, posicao_robot(2,3), s8).
link(s7, adjacente(2,3,3,3), s8).
link(s8, posicao_robot(3,3), s9).
link(s9, posicao_robot(3,3), s10). 
link(s0, adjacente(3,3,2,3), s10). 
link(s10, posicao_robot(2,3), s11).
link(s11, posicao_robot(2,3), s12).
link(s11, adjacente(2,3,3,3), s12).
link(s12, posicao_robot(3,3), sf).

link(s0, holding(vazio), s4).
link(s0, peca_em(5,2,3), s4).
link(s4, holding(5), s6).
link(s0, peca_em(vazio,2,2), s6). 
link(s6, holding(vazio), s9).
link(s0, peca_em(8,3,3), s9).
link(s9, holding(8), s11).
link(s4, peca_em(vazio,2,3), s11). 

link(s6, peca_em(5,2,2), sf).
link(s11, peca_em(8,2,3), sf).
link(s9, peca_em(vazio,3,3), sf). 
link(s11, holding(vazio), sf).
link(s12, posicao_robot(3,3), sf).

% Ordem
ordem(s0, s1). ordem(s1, s2). ordem(s2, s3). ordem(s3, s4).
ordem(s4, s5). ordem(s5, s6). ordem(s6, s7). ordem(s7, s8).
ordem(s8, s9). ordem(s9, s10). ordem(s10, s11). ordem(s11, s12).
ordem(s12, sf).