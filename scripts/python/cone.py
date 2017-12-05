from mpl_toolkits.mplot3d import Axes3D
from matplotlib import pyplot as plt
import numpy as np

fig = plt.figure(figsize=(16, 14))
ax = fig.add_subplot(111, projection='3d')

# Create the mesh in polar coordinates and compute corresponding Z.
r = np.linspace(0, 1.25, 50)
p = np.linspace(0, 2*np.pi, 50)
R, P = np.meshgrid(r, p)
Z = R

# Express the mesh in the cartesian system.
X, Y = R*np.cos(P), R*np.sin(P)

# get rid of the panes
ax.w_xaxis.set_pane_color((1.0, 1.0, 1.0, 0.0))
ax.w_yaxis.set_pane_color((1.0, 1.0, 1.0, 0.0))
ax.w_zaxis.set_pane_color((1.0, 1.0, 1.0, 0.0))

# Plot the surface.
ax.plot_surface(X, Y, Z, cmap=plt.cm.RdYlGn_r)

#ax.set_xlabel('dim1', fontsize=16)
#ax.set_ylabel('dim2', fontsize=16)
# ax.set_zlabel('fitness', fontsize=16)

plt.savefig("cone.pdf", bbox_inches="tight")
