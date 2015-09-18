/**
 * @license Copyright (c) 2003-2015, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 */

CKEDITOR.plugins.setLang( 'a11yhelp', 'tr', {
	title: 'Erişilebilirlik Talimatları',
	contents: 'Yardım içeriği. Bu pencereyi kapatmak için ESC tuşuna basın.',
	legend: [
		{
		name: 'Genel',
		items: [
			{
			name: 'Düzenleyici Araç Çubuğu',
			legend: 'Araç çubuğunda gezinmek için ${toolbarFocus} basın. TAB ve SHIFT+TAB ile önceki ve sonraki araç çubuğu grubuna taşıyın. SAĞ OK veya SOL OK ile önceki ve sonraki bir araç çubuğu düğmesini hareket ettirin. SPACE tuşuna basın veya araç çubuğu düğmesini etkinleştirmek için ENTER tuşna basın.'
		},

			{
			name: 'Diyalog Düzenleyici',
			legend:
				'Inside a dialog, press TAB to navigate to the next dialog element, press SHIFT+TAB to move to the previous dialog element, press ENTER to submit the dialog, press ESC to cancel the dialog. When a dialog has multiple tabs, the tab list can be reached either with ALT+F10 or with TAB as part of the dialog tabbing order. With tab list focused, move to the next and previous tab with RIGHT and LEFT ARROW, respectively.'  // MISSING
		},

			{
			name: 'İçerik Menü Editörü',
			legend: 'İçerik menüsünü açmak için ${contextMenu} veya UYGULAMA TUŞU\'na basın. Daha sonra SEKME veya AŞAĞI OK ile bir sonraki menü seçeneği taşıyın. SHIFT + TAB veya YUKARI OK ile önceki seçeneğe gider. Menü seçeneğini seçmek için SPACE veya ENTER tuşuna basın. Seçili seçeneğin alt menüsünü SPACE ya da ENTER veya SAĞ OK açın. Üst menü öğesini geçmek için ESC veya SOL OK ile geri dönün. ESC ile bağlam menüsünü kapatın.'
		},

			{
			name: 'Liste Kutusu Editörü',
			legend: 'Liste kutusu içinde, bir sonraki liste öğesine SEKME VEYA AŞAĞI OK ile taşıyın. SHIFT+TAB veya YUKARI önceki liste öğesi taşıyın. Liste seçeneği seçmek için SPACE veya ENTER tuşuna basın. Liste kutusunu kapatmak için ESC tuşuna basın.'
		},

			{
			name: 'Element Yol Çubuğu Editörü',
			legend: 'Elementlerin yol çubuğunda gezinmek için ${ElementsPathFocus} basın. SEKME veya SAĞ OK ile sonraki element düğmesine taşıyın. SHIFT+TAB veya SOL OK önceki düğmeye hareket ettirin. Editör içindeki elementi seçmek için ENTER veya SPACE tuşuna basın.'
		}
		]
	},
		{
		name: 'Komutlar',
		items: [
			{
			name: 'Komutu geri al',
			legend: '$(undo)\'ya basın'
		},
			{
			name: 'Komutu geri al',
			legend: '${redo} basın'
		},
			{
			name: ' Kalın komut',
			legend: '${bold} basın'
		},
			{
			name: ' İtalik komutu',
			legend: '${italic} basın'
		},
			{
			name: ' Alttan çizgi komutu',
			legend: '${underline} basın'
		},
			{
			name: ' Bağlantı komutu',
			legend: '${link} basın'
		},
			{
			name: ' Araç çubuğu Toplama komutu',
			legend: '${toolbarCollapse} basın'
		},
			{
			name: 'Önceki komut alanına odaklan',
			legend: 'Düzeltme imleçinden önce, en yakın uzaktaki alana erişmek için ${accessPreviousSpace} basın, örneğin: iki birleşik HR elementleri. Aynı tuş kombinasyonu tekrarıyla diğer alanlarada ulaşın.'
		},
			{
			name: 'Sonraki komut alanına odaklan',
			legend: 'Düzeltme imleçinden sonra, en yakın uzaktaki alana erişmek için ${accessNextSpace} basın, örneğin: iki birleşik HR elementleri. Aynı tuş kombinasyonu tekrarıyla diğer alanlarada ulaşın.'
		},
			{
			name: 'Erişilebilirlik Yardımı',
			legend: '${a11yHelp}\'e basın'
		}
		]
	}
	],
	backspace: 'Silme',
	tab: 'Sekme tuşu',
	enter: 'Gir tuşu',
	shift: '"Shift" Kaydırma tuşu',
	ctrl: '"Ctrl" Kontrol tuşu',
	alt: '"Alt" Anahtar tuşu',
	pause: 'Durdurma tuşu',
	capslock: 'Büyük harf tuşu',
	escape: 'Vazgeç tuşu',
	pageUp: 'Sayfa Yukarı',
	pageDown: 'Sayfa Aşağı',
	end: 'Sona',
	home: 'En başa',
	leftArrow: 'Sol ok',
	upArrow: 'Yukarı ok',
	rightArrow: 'Sağ ok',
	downArrow: 'Aşağı ok',
	insert: 'Araya gir',
	'delete': 'Silme',
	leftWindowKey: 'Sol windows tuşu',
	rightWindowKey: 'Sağ windows tuşu',
	selectKey: 'Seçme tuşu',
	numpad0: 'Nümerik 0',
	numpad1: 'Nümerik 1',
	numpad2: 'Nümerik 2',
	numpad3: 'Nümerik 3',
	numpad4: 'Nümerik 4',
	numpad5: 'Nümerik 5',
	numpad6: 'Nümerik 6',
	numpad7: 'Nümerik 7',
	numpad8: 'Nümerik 8',
	numpad9: 'Nümerik 9',
	multiply: 'Çarpma',
	add: 'Toplama',
	subtract: 'Çıkarma',
	decimalPoint: 'Ondalık işareti',
	divide: 'Bölme',
	f1: 'F1',
	f2: 'F2',
	f3: 'F3',
	f4: 'F4',
	f5: 'F5',
	f6: 'F6',
	f7: 'F7',
	f8: 'F8',
	f9: 'F9',
	f10: 'F10',
	f11: 'F11',
	f12: 'F12',
	numLock: 'Num Lk',
	scrollLock: 'Scr Lk',
	semiColon: 'Noktalı virgül',
	equalSign: 'Eşittir',
	comma: 'Virgül',
	dash: 'Eksi',
	period: 'Nokta',
	forwardSlash: 'İleri eğik çizgi',
	graveAccent: 'Üst tırnak',
	openBracket: 'Parantez aç',
	backSlash: 'Ters eğik çizgi',
	closeBracket: 'Parantez kapa',
	singleQuote: 'Tek tırnak'
} );
