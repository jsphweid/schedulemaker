package schedulemaker

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class DayPredictionController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond DayPrediction.list(params), model:[dayPredictionCount: DayPrediction.count()]
    }

    def show(DayPrediction dayPrediction) {
        respond dayPrediction
    }

    def create() {
        [DayPredictionInstance: new DayPrediction(params)]
    }

    def save() {
        def dayPredictionInstance = new DayPrediction(params)
        dayPredictionInstance.lastUpdate = new Date()
        if (!dayPredictionInstance.save()) {
            render(view: "add", modal: [dayPredictionInstance: dayPredictionInstance])
            return
        }
        redirect(action: "index", params: params)
    }

    def edit(DayPrediction dayPrediction) {
        respond dayPrediction
    }

    @Transactional
    def update(DayPrediction dayPrediction) {
        if (dayPrediction == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (dayPrediction.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond dayPrediction.errors, view:'edit'
            return
        }

        dayPrediction.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'dayPrediction.label', default: 'DayPrediction'), dayPrediction.id])
                redirect dayPrediction
            }
            '*'{ respond dayPrediction, [status: OK] }
        }
    }

    @Transactional
    def delete(DayPrediction dayPrediction) {

        if (dayPrediction == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        dayPrediction.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'dayPrediction.label', default: 'DayPrediction'), dayPrediction.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'dayPrediction.label', default: 'DayPrediction'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}