package com.furianrt.serenity

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
internal class SerenityApp : Application()



/*
* TODO Главный экран
* 1.Свайп записей для удаления
* 2.Множественный выбор записей для проставления тэгов(и еще для чего-нибудь)
* 3.Открытие других экранов через SharedElement
* */

/*
* TODO Персональный помощник
* 1.Выбор иконки помощника
* 2.Стараться не вынуждать юзера что-то печатать, а делать удобные кнопки для комуникации
* 3.Сделать боттом щит для коммуникации с аи на экране просмотра заметки
* */

/*
* TODO Настройки
* 1.Отключение помощника
* */

/*
* TODO Общее
* 1.(-incubated-) Проработать Accessibility
* 2.Перенести версии зависимостей в другой файл, например toml.
*   Сейчас некоторые версии приходится дублировать для convention plugin
* 3.
* */
