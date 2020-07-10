package com.dvproject.vertTerm.Service;

import java.util.List;

import com.dvproject.vertTerm.Model.Consumable;
import com.dvproject.vertTerm.Model.Status;

/**
 * @author Joshua Müller
 */
public interface ConsumableService extends BasicService<Consumable>{
	List<Consumable> getAllWithStatus (Status status);
}
