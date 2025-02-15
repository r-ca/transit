package me.nexryai.transit.templates

import io.ktor.server.html.*
import kotlinx.html.*
import me.nexryai.transit.entities.TransitInfo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.MutableList

class HeadTemplate: Template<HTML> {
    override fun HTML.apply() {
        head {
            title {
                +"Ktor"
            }
            meta {
                charset = "utf-8"
            }
            script(src = "/static/main.js") {}

            link {
                rel = "stylesheet"
                href = "/style.css"
            }

            link {
                rel = "preconnect"
                href = "https://fonts.bunny.net"
            }

            link {
                rel = "stylesheet"
                href = "https://fonts.bunny.net/css2??family=Josefin+Sans:ital,wght@0,100..700;1,100..700&family=Noto+Sans+JP:wght@100..900&family=Poppins:ital,wght@0,100;0,200;0,300;0,400;0,500;0,600;0,700;0,800;0,900;1,100;1,200;1,300;1,400;1,500;1,600;1,700;1,800;1,900&family=Ubuntu:ital,wght@0,300;0,400;0,500;0,700;1,300;1,400;1,500;1,700&display=swap"
            }

            link {
                rel = "preconnect"
                href = "https://cdn.jsdelivr.net"
            }

            link {
                rel = "stylesheet"
                href = "https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@latest/dist/tabler-icons.min.css"
            }

            meta {
                name = "viewport"
                content = "width=device-width, initial-scale=1.0"
            }
        }
    }
}

class ContentTemplate: Template<FlowContent> {
    override fun FlowContent.apply() {
        article {
            h2 {
                +"乗換案内へようこそ"
            }
            p {
                +"フォームに入力して検索しましょう。"
            }
            div(classes = "form"){
                div(classes = "form-element") {
                    p(classes = "form-label") {
                        i("ti ti-plane-departure form-icon")
                        +"出発地となるバス停・駅"
                    }
                    input {
                        id = "from"
                        type = InputType.text
                        placeholder = "出発地"
                    }
                }

                div(classes = "form-element") {
                    p(classes = "form-label") {
                        i("ti ti-plane-arrival form-icon")
                        +"到着地となるバス停・駅"
                    }
                    input {
                        id = "to"
                        type = InputType.text
                        placeholder = "到着地"
                    }
                }

                div(classes = "form-element") {
                    p(classes = "form-label") {
                        i("ti ti-clock-hour-10 form-icon")
                        +"目標日時"
                    }
                    input(classes = "form-radio") {
                        id = "timeModeDeparture"
                        type = InputType.radio
                        name = "timeMode"
                        value = "d"
                        checked = true
                    }
                    label(classes = "form-radio-label") {
                        htmlFor = "timeModeDeparture"
                        +"出発"
                    }

                    input(classes = "form-radio") {
                        id = "timeModeArrival"
                        type = InputType.radio
                        name = "timeMode"
                        value = "a"
                    }
                    label(classes = "form-radio-label") {
                        htmlFor = "timeModeArrival"
                        +"到着"
                    }

                    input {
                        id = "date"
                        type = InputType.date
                        placeholder = "日付"
                    }

                    input {
                        id = "time"
                        type = InputType.time
                        placeholder = "時刻"
                    }
                }

                div {
                    id = "error"
                    p {
                        id = "errorLabel"
                    }
                }

                div(classes = "form-button") {
                    button {
                        id = "search"
                        onClick = "jumpToResult()"
                        i("ti ti-search button-icon")
                        +"検索"
                    }
                }
            }
        }
    }
}

class WelcomePageTemplate: Template<HTML> {
    private val head = TemplatePlaceholder<HeadTemplate>()
    private val content = TemplatePlaceholder<ContentTemplate>()
    override fun HTML.apply() {
        insert(HeadTemplate(), head)
        body {
            div {
                id = "app"
                div {
                    id = "header"
                    h1 {
                        +"TransitKt"
                    }
                }

                div {
                    id = "content"
                    insert(ContentTemplate(), content)
                }
            }
        }
    }
}

class ResultTemplate(private val result: TransitInfo): Template<FlowContent> {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    override fun FlowContent.apply() {
        h3 {
            +"Result"
        }
        for ((i, transfer) in result.transfers.withIndex()) {
            div(classes = "transfer") {
                div(classes = "transfer-station") {
                    h4(classes = "transfer-station-name") {
                        if (i == 0) {
                            span(classes = "transfer-depart-time") {
                                +"${transfer.depart} "
                            }
                            i("ti ti-flag result-icon")
                            span(classes = "transfer-station-name-subtext") {
                                +" 出発地 "
                            }
                        } else if (i == result.transfers.size - 1) {
                            span(classes = "transfer-depart-time") {
                                +"${transfer.depart} "
                            }
                            i("ti ti-map-check result-icon")
                            span(classes = "transfer-station-name-subtext") {
                                +" 目的地 "
                            }
                        } else {
                            i("ti ti-map-pin result-icon")
                            span(classes = "transfer-station-name-subtext") {
                                +" 乗換 "
                            }
                        }

                        +transfer.stationName
                    }
                    p(classes = "transfer-platform-info") {
                        if (i != result.transfers.size - 1 && transfer.train?.departPlatform?.isNotEmpty() == true) {
                            i("ti ti-flag-3 result-icon")
                            +" ${transfer.train?.departPlatform} "
                        }
                    }
                }

                val cssStr = if (transfer.train != null) {
                    transfer.train!!.style
                } else {
                    ""
                }

                if (transfer.train != null){
                    div(classes = "transfer-train") {
                        style = cssStr
                        p {
                            i("ti ti-plane-departure result-icon")
                            +" ${transfer.depart} "

                            if (transfer.train!!.destination.isEmpty()) {
                                +"徒歩"
                            } else {
                                span(classes = "train-info") {
                                    +"${transfer.train!!.displayInfo} ${transfer.train!!.destination}"
                                }
                            }
                        }
                        if (transfer.train!!.numOfStops != 0) {
                            p(classes = "num-of-stops") {
                                i("ti ti-map-pins result-icon")
                                +" ${transfer.train!!.numOfStops}駅乗車"
                            }
                        }
                        if (i != result.transfers.size - 1) {
                            p {
                                i("ti ti-plane-arrival result-icon")
                                +" ${result.transfers[i + 1].arrive} "
                            }
                        }
                    }
                }
            }
        }
        p(classes = "generated-at") {
            +"Generated at ${LocalDateTime.now().format(dateFormatter)}"
        }
    }
}

class ResultPageTemplate(private val result: MutableList<TransitInfo>): Template<HTML> {
    private val head = TemplatePlaceholder<HeadTemplate>()
    private val content = TemplatePlaceholder<ResultTemplate>()
    override fun HTML.apply() {
        insert(HeadTemplate(), head)
        body {
            div {
                id = "app"
                div {
                    id = "header"
                    h1 {
                        +"TransitKt"
                    }
                }

                div {
                    id = "contents_container"
                    for (r in result) {
                        div {
                            classes = setOf("content")
                            insert(ResultTemplate(r), content)
                        }
                    }
                }
            }
        }
    }
}
