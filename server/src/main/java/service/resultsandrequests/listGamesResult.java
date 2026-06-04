package service.resultsandrequests;

import model.GameData;

import java.util.ArrayList;

public record listGamesResult(ArrayList<GameData> games) implements Result{
}
