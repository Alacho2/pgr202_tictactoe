package play.alacho.no.game

import android.util.Log

class GameLogic(val humanPlayer: Player, val botPlayer: Player) {

  val board: Array<Player?> = arrayOfNulls(9)

  fun nextMove() = findNextMove().apply {
    board[this] = botPlayer
  }

  private fun findNextMove(): Int{
    val filledSpots = board.mapIndexedNotNull { index, value -> value?.let { index } }
    return if(filledSpots.size == 1){
      firstMove(filledSpots.first())
    } else {
      // Try to find a win condition
      val winTile = findWinConditionFor(botPlayer, 1)
      // If we can't find a loss condition check for a loss condition and block it
      val lossTile = findWinConditionFor(humanPlayer, 1)

      val possibleWinCondition = findWinConditionFor(botPlayer, 2)
      when {
        winTile != null -> winTile
        lossTile != null -> lossTile
        possibleWinCondition != null -> possibleWinCondition
        else -> 0.until(3).flatMap { horizontalIndexesFor(it, null) }.first() // No possible win conditions, take first open
      }
    }
  }

  private fun firstMove(firstMove: Int) =  when(firstMove) { // check first placement
    in arrayOf(0,2,6,8) -> 4
    in arrayOf(1,3) -> 8
    else -> 0
  }

  fun findWinner(): Player? = when {
    findWinConditionFor(botPlayer, 3) == -1 -> botPlayer
    findWinConditionFor(humanPlayer, 3) == -1 -> humanPlayer
    else -> null
  }

  private fun findWinConditionFor(targetPlayer: Player, requiredSpots: Int): Int? {
    0.until(3).forEach{ idx ->
      val openSpotsHorizontal = horizontalIndexesFor(idx, null)
      val playerSpotsHorizontal = horizontalIndexesFor(idx, targetPlayer)
      if (openSpotsHorizontal.size == 3-requiredSpots && playerSpotsHorizontal.size == requiredSpots) {
        return openSpotsHorizontal.first()
      }
      val openSpotsVertical = verticalIndexesFor(idx, null)
      val playerSpotsVertical = verticalIndexesFor(idx, targetPlayer)
      if (openSpotsVertical.size == 3-requiredSpots && playerSpotsVertical.size == requiredSpots) {
        return openSpotsVertical.first()
      }
    }
    return null
  }

  private fun horizontalIndexesFor(row: Int, player: Player?) =
    (row * 3).until(row * 3 + 3).filter { board[it] == player }

  /*fun horizontalIndexesFor(row: Int, player: Player?) = mutableListOf<Int>().apply {
    for (idx in (row * 3).until(row * 3 + 3)) {
      if (board[idx] == player) {
        add(idx)
      }
    }
  }*/

  private fun verticalIndexesFor(column: Int, player: Player?) =
    0.until(3).map { it * 3 + column }.filter { board[it] == player }
  /*fun verticalIndexesFor(column: Int, player: Player?) = mutableListOf<Int>().apply {
    for (idx in 0.until(3).map { it * 3 + column }) {
      if (board[idx] == player) {
        add(idx)
      }
    }
  }*/
}