---
name: New translation
about: All strings I will need to add a new translation.
title: "[Translation] Language: ..."
labels: enhancement
assignees: mimoguz

---

# Bundle_??.properties

**Note:** This file uses Java MessageFormat syntax.

* [Baeldung article](https://www.baeldung.com/java-localization-messages-formatting)
* [JavaDoc](https://docs.oracle.com/javase/7/docs/api/java/text/MessageFormat.html)

## File contents

    start=Start
    fromStack=You took {0,choice,0#no cards|1#one card|1<{0,number,integer} cards} from the stack.
    usedUndo=You {0,choice,0#didn't use undo at all|1#used undo once|2#used undo twice|2<used undo {0,number,integer} times}.
    longestChain=Your longest chain had {0,choice,0#zero cards|1#one card|1<{0,number,integer} cards}.
    newGame=New game
    exit=Exit
    return=Return to game
    darkTheme=Use dark theme
    showAll=Show all cards
    won=You won!
    stalled=No more moves!
    undoLast=Undo last move
    save=Save
    cancel=Cancel
    emptyDiscard=Start with an empty discard pile
    basicLayout=Three peaks
    inverted2ndLayout=Two peaks and a valley
    diamondsLayout=Diamonds
    layout=Layout for new games:
    options=Options
    statistics=Statistics
    statAll=All
    statGames=Games
    statWins=Wins
    statLongestChain=The longest chain
    cardAnimation=Card animation:
    Blink=Blink
    Dissolve=Dissolve
    FadeOut=Fade out
    decor=Card design:
    close=Close
    about=About

# full_description.txt

Text: An open-source tri peaks solitaire game with multiple layout options. Currently only has randomly generated games.

Translation: ...

# short_description.txt

Text: A tri peaks solitaire game.

Translation: ...

# Other strings

Note: Following strings are used in [this image](https://github.com/mimoguz/tripeaks-gdx/blob/main/fastlane/metadata/android/en-US/images/phoneScreenshots/2.png)

Text: Deal
Translation: ...

Text: Menu
Translation: ...

Text: Undo
Translation: ...
