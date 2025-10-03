package com.chaeniiz.kakaobooksearch.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 단방향 MVVM 패턴을 위한 기본 ViewModel
 * 
 * @param State UI 상태를 나타내는 데이터 클래스
 * @param Intent 사용자 액션을 나타내는 sealed class
 * @param Effect 단발성 이벤트를 나타내는 sealed class
 */
abstract class BaseViewModel<State : UiState, Intent : UiIntent, Effect : UiEffect>(
    initialState: State
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<Effect>()
    val effect = _effect.asSharedFlow()

    /**
     * Intent 처리를 위한 추상 메서드
     * 각 ViewModel에서 구현해야 함
     */
    abstract fun handleIntent(intent: Intent)

    /**
     * State 업데이트
     */
    protected fun updateState(update: (State) -> State) {
        _state.value = update(_state.value)
    }

    /**
     * 현재 State 조회
     */
    protected val currentState: State
        get() = _state.value

    /**
     * Effect 발생
     */
    protected fun sendEffect(effect: Effect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}
