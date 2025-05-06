from matplotlib import pyplot as plt
import seaborn as sns


def as_heatmap(cm: any, epoch: int, dir: str = "", cmap: str = 'RdYlGn'):
    _fig, _ax = plt.subplots()
    sns.heatmap(cm, square=True, fmt='.0f', cmap=cmap, cbar=False, annot=True, mask=cm == 0)
    _ax.xaxis.tick_top()
    _ax.xaxis.set_label_position('top')
    _fig.set_size_inches([10, 10])
    plt.xlabel('Predicted Values')
    plt.ylabel('True Values')
    _fig.savefig(f'{dir}class_results_epoch_{epoch}.png', bbox_inches='tight')
    plt.close(_fig)
