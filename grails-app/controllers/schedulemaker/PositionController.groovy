package schedulemaker

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class PositionController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [positionList: Position.list(params), positionCount: Position.count()]
    }

    def show(Position position  ) {
        respond position
    }

    def create() {
        respond new Position(params)
    }

    @Transactional
    def save() {
        def position = new Position(params)
        position.lastUpdate = new Date()
        if (position == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (position.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond position.errors, view:'create'
            return
        }

        position.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'position.label', default: 'Position'), position.id])
                redirect position
            }
            '*' { respond position, [status: CREATED] }
        }
    }

    def edit(Position position) {
        respond position
    }

    @Transactional
    def update(Position position) {

        position.lastUpdate = new Date()

        if (position == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (position.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond position.errors, view:'edit'
            return
        }

        position.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'position.label', default: 'Position'), position.id])
                redirect position
            }
            '*'{ respond position, [status: OK] }
        }
    }

    @Transactional
    def delete(Position position) {

        if (position == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        position.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'position.label', default: 'Position'), position.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'position.label', default: 'Position'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
