package de.bripkens.ha.reporting

import akka.actor.{Props, ActorLogging, Actor}
import com.fasterxml.jackson.databind.ObjectMapper
import de.bripkens.ha.ComponentStatus.ComponentStatus
import de.bripkens.ha.{ComponentStatus, ComponentStatusUpdate, ConsoleReporterConfig}

import scala.collection.mutable

object ConsoleReporter {

  def props(mapper: ObjectMapper, config: ConsoleReporterConfig) = Props(new ConsoleReporter(mapper, config))

}

class ConsoleReporter(val mapper: ObjectMapper, val config: ConsoleReporterConfig) extends Actor
                                                                                   with ActorLogging {

  val componentStatus = new mutable.HashMap[String, ComponentStatus]()

  override def receive: Receive = {
    case ComponentStatusUpdate(component, ComponentStatus.OKAY, _) => {
      if (!componentStatus.get(component.id).contains(ComponentStatus.OKAY)) {
        log.info(s"${component.name} is okay.")
        componentStatus.put(component.id, ComponentStatus.OKAY)
      }
    }
    case ComponentStatusUpdate(component, ComponentStatus.UNHEALTHY, _) => {
      if (!componentStatus.get(component.id).contains(ComponentStatus.UNHEALTHY)) {
        log.info(s"${component.name} has some issues.")
        componentStatus.put(component.id, ComponentStatus.UNHEALTHY)
      }
    }
    case ComponentStatusUpdate(component, ComponentStatus.NOT_REACHABLE, _) => {
      if (!componentStatus.get(component.id).contains(ComponentStatus.NOT_REACHABLE)) {
        log.info(s"${component.name} cannot be reached.")
        componentStatus.put(component.id, ComponentStatus.NOT_REACHABLE)
      }
    }
    case unsupported => log.error(s"Unsupported message received: $unsupported")
  }

}
