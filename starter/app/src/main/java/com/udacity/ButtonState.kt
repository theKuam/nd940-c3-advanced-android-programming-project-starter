package com.udacity


sealed class ButtonState {

    object Init : ButtonState()
    object Clicked : ButtonState()
    object Loading : ButtonState()
    object Completed : ButtonState()
}