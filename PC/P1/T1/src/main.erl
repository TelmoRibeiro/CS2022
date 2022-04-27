- module(main).
- export([start/0, server/0, client/2]).
- import(lists, [sort/2, sort/1]).

client(Server, Message) ->
    Server ! {self(), Message},
    receive 
        {Server, Result} -> Result 
    end.

start() -> spawn(main, server, []).

server() -> 
    receive 
        {From, {sum, PA, PB}} -> 
            LA = lists:sort(fun({VARA, _, EXPA}, {VARB, _, EXPB}) -> if VARA < VARB -> true; VARA > VARB -> false; VARA == VARB -> EXPA =< EXPB end end, PA),
            LB = lists:sort(fun({VARA, _, EXPA}, {VARB, _, EXPB}) -> if VARA < VARB -> true; VARA > VARB -> false; VARA == VARB -> EXPA =< EXPB end end, PB),
            From ! {self(), sum(simplify(LA), simplify(LB))},
            server();
        {From, {sub, PA, PB}} ->
            LA = lists:sort(fun({VARA, _, EXPA}, {VARB, _, EXPB}) -> if VARA < VARB -> true; VARA > VARB -> false; VARA == VARB -> EXPA =< EXPB end end, PA),
            LB = lists:sort(fun({VARA, _, EXPA}, {VARB, _, EXPB}) -> if VARA < VARB -> true; VARA > VARB -> false; VARA == VARB -> EXPA =< EXPB end end, PB),
            From ! {self(), sub(simplify(LA), simplify(LB))},
            server();
        {From, {mul, PA, PB}} ->
            LA = lists:sort(fun({VARA, _, EXPA}, {VARB, _, EXPB}) -> if VARA < VARB -> true; VARA > VARB -> false; VARA == VARB -> EXPA =< EXPB end end, PA),
            LB = lists:sort(fun({VARA, _, EXPA}, {VARB, _, EXPB}) -> if VARA < VARB -> true; VARA > VARB -> false; VARA == VARB -> EXPA =< EXPB end end, PB),
            From ! {self(), mul(simplify(LA), simplify(LB))},
            server()
        end.


simplify([]) -> [];
simplify([Head|[]]) -> [Head|[]];
simplify([{VarA, CoefA, ExpA}|[{VarB, CoefB, ExpB}|Tail]]) ->
    if 
        (VarA == VarB) and (ExpA == ExpB) ->
            [{VarA, CoefA + CoefB, ExpA}] ++ simplify(Tail);
        (VarA =/= VarB) or (ExpA =/= ExpB) ->
            [{VarA, CoefA, ExpA}] ++ simplify([{VarB, CoefB, ExpB}|Tail])
    end.


sum([], []) -> [];
sum(LA, []) -> LA;
sum([], LB) -> LB;
sum([{VarA, CoefA, ExpA}|TailA], [{VarB, CoefB, ExpB}|TailB]) ->
    if 
        (VarA == VarB) and (ExpA == ExpB) ->
            [{VarA, CoefA + CoefB, ExpA}] ++ sum(TailA, TailB);
        ((VarA == VarB) and (ExpA =< ExpB)) or (VarA < VarB) ->
            [{VarA, CoefA, ExpA}] ++ sum(TailA, [{VarB, CoefB, ExpB}|TailB]);
        ((VarA == VarB) and (ExpA >  ExpB)) or (VarA > VarB) ->
            [{VarB, CoefB, ExpB}] ++ sum([{VarA, CoefA, ExpA}|TailA], TailB)
    end.


neg([]) -> [];
neg([{Var, Coef, Exp}|Tail]) -> [{Var, -Coef, Exp}] ++ neg(Tail).

sub(P1, P2) ->
    NP2 = neg(P2),
    sum(P1, NP2).


mulAux(_, []) -> [];
mulAux({VarA, CoefA, ExpA}, [{VarB, CoefB, ExpB}|TailB]) ->
    if
        VarA == VarB ->
            [{VarA, CoefA * CoefB, ExpA + ExpB}] ++ mulAux({VarA, CoefA, ExpA}, TailB);
        VarA =< VarB ->
            [{{VarA, VarB}, CoefA * CoefB, {ExpA, ExpB}}] ++ mulAux({VarA, CoefA, ExpA}, TailB);
        VarA > VarB  ->
            [{{VarB, VarA}, CoefA * CoefB, {ExpB, ExpA}}] ++ mulAux({VarA, CoefA, ExpA}, TailB)
    end.

mul([], _) -> [];
mul([HeadA|TailA], PolyB) -> mulAux(HeadA, PolyB) ++ mul(TailA, PolyB).

% main:client(PID,{sum, [{x,2,1}, {y,2,3}], [{x,1,1}, {y,3,3}]}).                   = [{x, 3, 1}, {y,5,3}
% main:client(PID,{sub, [{x,2,1}, {y,2,3}], [{x,1,1}, {y,3,3}]}).                   = [{x, 1, 1}, {y,-1,3}
% main:client(PID,{mul, [{x,2,1}, {y,2,3}], [{x,1,1}, {y,3,3}]}).                   = [{x, 2, 2}, {{x,y},6,{1,3}, {{x,y},2,{1,3}}, {y,6,6}]

% main:client(PID2,{sum, [{{x,y},2,{2,2}}, {y,2,3}], [{{y,x},1,{2,2}}, {y,3,3}]}).  = [{y,5,3},{{x,y},2,{2,2}},{{y,x},1,{2,2}}]