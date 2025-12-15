package org.pampasim.dialog;
import java.util.List;
import java.util.Optional;

public record AddSpecOrModulesRecord(Optional<AddModulesRecord> modulesRecord, Optional<String> specPath){
}
