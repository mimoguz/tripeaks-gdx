# TO DO:

* _Fix:_ Win/lose icons have odd widths, but the spacing between dialog buttons is even. This causes the icon alignment to be off by one pixel.
* _Fix:_ Statistics dialog alignment when no games have been played yet.
* _Change:_ Should ```undo``` just decrease the current chain length or reset?

## Version 1.2 and beyond

* Restart current game option, both from the menu and the stalled dialog __(1.2)__. This should be same with starting a new game and count as a lose.
* Documentation/help: I can create a wiki page and add a link to it in the about dialog __(1.2)__.
* "System" theme option. Maybe I can just check the system theme on start and pass that to ```Main``` __(1.2)__. _[Draf implementation on the ```system_theme``` branch.]_
* A better card back design UI: A carousel control or a pop-up maybe? __(> 1.2)__

### Scoring (> 1.2)
Excerpt from [this page](https://anytime.games/tri-peaks-solitaire-rules/):

> give yourself one point for the first card you discard. Each subsequent discard gets you one more point than the previous one. For example, your second discard earns you two points, on the third discard, you'll get three points, and so on.

> If you find yourself having to take a card from the stock pile, the sequence ends. You’ll also need to subtract five points from your score, and the new sequence begins again at one point.
 
> You earn 15 points the first time you discard the top card of any peak. If you do that on the final peak, you'll rack up a whopping 30 points. This also clears the tableau and wins the game.
