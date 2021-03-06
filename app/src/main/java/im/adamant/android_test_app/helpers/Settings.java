package im.adamant.android_test_app.helpers;

import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

import im.adamant.android_test_app.core.entities.ServerNode;
import im.adamant.android_test_app.rx.ObservableRxList;
import io.reactivex.disposables.Disposable;

public class Settings {
    private static final String NODES_KEY = "nodes_key";
    private ObservableRxList<ServerNode> nodes = new ObservableRxList<>();

    private SharedPreferences preferences;

    public Settings(SharedPreferences preferences) {
        this.preferences = preferences;

        loadNodes();
    }

    public void addNode(ServerNode node) {
        nodes.add(node);
        updateNodes();
    }

    public void removeNode(ServerNode node) {
        if (nodes.contains(node)){
            nodes.remove(node);
            updateNodes();
        }
    }

    public ObservableRxList<ServerNode> getNodes() {
        return nodes;
    }

    private void updateNodes() {
        Disposable subscribe = nodes.getCurrentList()
                .map(ServerNode::getUrl)
                .toList()
                .subscribe((list) -> {
                    Set<String> set = new HashSet<>(list);
                    preferences
                            .edit()
                            .putStringSet(NODES_KEY, set)
                            .apply();
                });
    }

    private Set<String> getDefaultNodes() {
        Set<String> defaults = new HashSet<>();
        defaults.add("https://clown.adamant.im");
        defaults.add("https://lake.adamant.im");
        defaults.add("https://endless.adamant.im");

        return defaults;
    }

    private void loadNodes(){
        Set<String> nodeUrls = preferences.getStringSet(NODES_KEY, getDefaultNodes());

        if (nodeUrls.size() == 0){
            nodeUrls = getDefaultNodes();
            preferences
                    .edit()
                    .putStringSet(NODES_KEY, nodeUrls)
                    .apply();
        }

        for (String nodeUrl : nodeUrls) {
            nodes.add(new ServerNode(nodeUrl));
        }
    }
}
