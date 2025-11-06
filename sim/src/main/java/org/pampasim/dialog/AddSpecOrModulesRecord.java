package org.pampasim.dialog;
import java.util.List;
import java.util.Optional;

public record AddSpecOrModulesRecord(AddModulesRecord modulesRecord, Optional<String> specPath){
}
