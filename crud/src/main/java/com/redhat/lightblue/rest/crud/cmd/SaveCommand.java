/*
 Copyright 2013 Red Hat, Inc. and/or its affiliates.

 This file is part of lightblue.

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.redhat.lightblue.rest.crud.cmd;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.redhat.lightblue.util.Error;
import com.redhat.lightblue.Response;
import com.redhat.lightblue.crud.SaveRequest;
import com.redhat.lightblue.mediator.Mediator;
import com.redhat.lightblue.rest.CallStatus;
import com.redhat.lightblue.rest.crud.RestCrudConstants;
import com.redhat.lightblue.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nmalik
 */
public class SaveCommand extends AbstractRestCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(SaveCommand.class);

    private final String entity;
    private final String version;
    private final String request;

    public SaveCommand(String entity, String version, String request) {
        this(null, entity, version, request);
    }

    public SaveCommand(Mediator mediator, String entity, String version, String request) {
        super(mediator);
        this.entity = entity;
        this.version = version;
        this.request = request;
    }

    @Override
    public CallStatus run() {
        LOGGER.debug("run: entity={}, version={}", entity, version);
        Error.reset();
        Error.push("rest");
        Error.push(getClass().getSimpleName());
        Error.push(entity);
        try {
            SaveRequest ireq = getJsonTranslator().parse(SaveRequest.class, JsonUtils.json(request));
            validateReq(ireq, entity, version);
            addCallerId(ireq);
            Response r = getMediator().save(ireq);
            return new CallStatus(r);
        } catch (Error e) {
            LOGGER.error("save failure: {}", e);
            return new CallStatus(e);
        } catch (Exception e) {
            LOGGER.error("save failure: {}", e);
            return new CallStatus(Error.get(RestCrudConstants.ERR_REST_SAVE, e.toString()));
        }
    }
}
