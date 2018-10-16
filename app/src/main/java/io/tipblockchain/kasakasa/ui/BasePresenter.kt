package io.tipblockchain.kasakasa.ui

interface BasePresenter<V: BaseView> {
    var view: V?

    fun attach(view: V) {
        this.view = view
    }
    fun detach() {
        this.view = null
    }
}